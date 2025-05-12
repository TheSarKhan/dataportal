package org.example.dataprotal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecaptchaConfig {

    @Value("${recaptcha.secret.key}")
    private String recaptchaSecret;

    private static final String GOOGLE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String token) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(GOOGLE_VERIFY_URL);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("secret", recaptchaSecret));
            params.add(new BasicNameValuePair("response", token));
            post.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> map = mapper.readValue(response.getEntity().getContent(), Map.class);
            return (Boolean) map.get("success");

        } catch (Exception e) {
            return false;
        }
    }
}

