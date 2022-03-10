package me.aki.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.aki.demo.config.AppConfig;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.Objects;

public class TranslateUtil {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TranslateUtil() {
    }

    public static String translateToZh(String s) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("auth_key", AppConfig.CONFIG.getDeeplAuthKey())
                .add("target_lang", "ZH")
                .add("text", s)
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url("https://api-free.deepl.com/v2/translate")
                .build();
        String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
        return objectMapper.readTree(body).get("translations").get(0).get("text").asText();
    }
}
