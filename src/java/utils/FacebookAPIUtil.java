package utils;

import models.FacebookPostRequest;
import models.FacebookPostResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

public class FacebookAPIUtil {
    private static final String FACEBOOK_API_BASE_URL = "https://graph.facebook.com/v18.0";
    private HttpClient httpClient;
    private Gson gson;
    
    public FacebookAPIUtil() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
    }
    
    /**
     * Đăng bài lên Facebook Page sử dụng Apache HttpClient
     */
    public FacebookPostResponse postToFacebookPage(FacebookPostRequest request) {
        try {
            // Nếu có hình ảnh, đăng dưới dạng photo post
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                return postPhotoToFacebookPage(request);
            } else {
                // Đăng dưới dạng text post
                return postTextToFacebookPage(request);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi đăng bài lên Facebook: " + e.getMessage());
            e.printStackTrace();
            return new FacebookPostResponse(false, "Lỗi khi gọi API Facebook: " + e.getMessage(), "API_ERROR", e.toString());
        }
    }
    
    /**
     * Đăng text post lên Facebook Page
     */
    private FacebookPostResponse postTextToFacebookPage(FacebookPostRequest request) throws Exception {
        String url = FACEBOOK_API_BASE_URL + "/" + request.getPageId() + "/feed";
        
        HttpPost httpPost = new HttpPost(url);
        
        // Tạo parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("message", request.getMessage()));
        
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            params.add(new BasicNameValuePair("link", request.getLink()));
        }
        
        params.add(new BasicNameValuePair("access_token", request.getPageAccessToken()));
        
        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        
        // Gửi request
        HttpResponse response = httpClient.execute(httpPost);
        String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        
        return parseResponse(responseText);
    }
    
    /**
     * Đăng single photo lên Facebook Page
     */
    private FacebookPostResponse postPhotoToFacebookPage(FacebookPostRequest request) throws Exception {
        String url = FACEBOOK_API_BASE_URL + "/" + request.getPageId() + "/photos";
        
        HttpPost httpPost = new HttpPost(url);
        
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("message", request.getMessage()));
        params.add(new BasicNameValuePair("url", request.getImageUrls().get(0)));
        
        if (request.getLink() != null && !request.getLink().isEmpty()) {
            params.add(new BasicNameValuePair("link", request.getLink()));
        }
        
        params.add(new BasicNameValuePair("access_token", request.getPageAccessToken()));
        
        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        
        HttpResponse response = httpClient.execute(httpPost);
        String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        
        return parseResponse(responseText);
    }
    
    /**
     * Parse response từ Facebook API
     */
    private FacebookPostResponse parseResponse(String responseText) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseText).getAsJsonObject();
            
            if (jsonResponse.has("id")) {
                // Thành công
                String postId = jsonResponse.get("id").getAsString();
                return new FacebookPostResponse(true, postId, "Đăng bài thành công");
            } else if (jsonResponse.has("error")) {
                // Có lỗi
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMessage = error.has("message") ? error.get("message").getAsString() : "Lỗi không xác định";
                String errorCode = error.has("code") ? error.get("code").getAsString() : "UNKNOWN";
                String errorType = error.has("type") ? error.get("type").getAsString() : "";
                
                return new FacebookPostResponse(false, errorMessage, errorCode, errorType);
            } else {
                return new FacebookPostResponse(false, "Response không hợp lệ từ Facebook", "INVALID_RESPONSE", responseText);
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi parse Facebook response: " + e.getMessage());
            e.printStackTrace();
            return new FacebookPostResponse(false, "Lỗi parse response: " + e.getMessage(), "PARSE_ERROR", responseText);
        }
    }
    
    /**
     * Validate Facebook Page Access Token
     */
    public boolean validatePageAccessToken(String pageId, String accessToken) {
        try {
            String url = FACEBOOK_API_BASE_URL + "/" + pageId + "?access_token=" + 
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonObject jsonResponse = JsonParser.parseString(responseText).getAsJsonObject();
                
                // Kiểm tra có thông tin page không
                return jsonResponse.has("id") && jsonResponse.has("name");
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Lỗi validate Facebook token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy thông tin Facebook Page
     */
    public JsonObject getPageInfo(String pageId, String accessToken) {
        try {
            String url = FACEBOOK_API_BASE_URL + "/" + pageId + 
                        "?fields=id,name,category,fan_count,link&access_token=" + 
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return JsonParser.parseString(responseText).getAsJsonObject();
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi get page info: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Kiểm tra quyền của access token
     */
    public List<String> getTokenPermissions(String accessToken) {
        List<String> permissions = new ArrayList<>();
        
        try {
            String url = FACEBOOK_API_BASE_URL + "/me/permissions?access_token=" + 
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonObject jsonResponse = JsonParser.parseString(responseText).getAsJsonObject();
                
                if (jsonResponse.has("data")) {
                    jsonResponse.getAsJsonArray("data").forEach(element -> {
                        JsonObject permission = element.getAsJsonObject();
                        if (permission.has("permission") && 
                            "granted".equals(permission.get("status").getAsString())) {
                            permissions.add(permission.get("permission").getAsString());
                        }
                    });
                }
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi get token permissions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Cleanup HttpClient resources
     */
    public void close() {
        try {
            if (httpClient instanceof AutoCloseable) {
                ((AutoCloseable) httpClient).close();
            }
        } catch (Exception e) {
            System.err.println("Lỗi close HttpClient: " + e.getMessage());
            e.printStackTrace();
        }
    }
}