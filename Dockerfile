# GIAI ĐOẠN 1: Build dự án bằng Maven để tạo file .war
# Đảm bảo phiên bản openjdk khớp với phiên bản Java trong pom.xml của bạn (ví dụ: 11, 17, 21)
FROM maven:3.8-openjdk-11 AS build

# Tạo thư mục làm việc và sao chép mã nguồn
WORKDIR /app
COPY . .

# Chạy lệnh build của Maven. Nó sẽ tự tìm pom.xml và tải các thư viện
RUN mvn clean package -DskipTests


# GIAI ĐOẠN 2: Chạy ứng dụng trên máy chủ Tomcat
# Đảm bảo phiên bản jdk ở đây cũng khớp
FROM tomcat:9.0-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy file .war đã được build ở giai đoạn 1 vào Tomcat
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]