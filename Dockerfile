FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# Build with optimizations
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17-temurin

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# ✅ SECURITY: Disable Tomcat shutdown port
RUN sed -i 's/port="8005"/port="-1"/g' /usr/local/tomcat/conf/server.xml

# ✅ SECURITY: Remove unnecessary Tomcat docs/examples
RUN rm -rf /usr/local/tomcat/webapps/docs \
    /usr/local/tomcat/webapps/examples \
    /usr/local/tomcat/webapps/host-manager \
    /usr/local/tomcat/webapps/manager

# ✅ PERFORMANCE: JVM optimizations
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200"

# ✅ PERFORMANCE: Tomcat optimizations
ENV CATALINA_OPTS="-Dorg.apache.catalina.startup.EXIT_ON_INIT_FAILURE=true"

# Copy WAR file
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Create non-root user for security
RUN groupadd -r tomcat && useradd -r -g tomcat tomcat
RUN chown -R tomcat:tomcat /usr/local/tomcat
USER tomcat

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

CMD ["catalina.sh", "run"]