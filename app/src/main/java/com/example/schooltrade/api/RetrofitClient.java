package com.example.schooltrade.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 模拟器用 10.0.2.2 访问宿主机，真机改为电脑局域网IP
    private static final String BASE_URL = "http://115.29.198.35:8081/";
    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public static void resetInstance() {
        instance = null;
    }

    /** 去掉URL末尾的斜杠 */
    public static String getBaseUrl() {
        String url = BASE_URL;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /** 把服务器返回的相对路径转为完整URL */
    public static String fullUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return null;
        if (relativePath.startsWith("http")) return relativePath;
        return getBaseUrl() + relativePath;
    }
}
