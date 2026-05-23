package com.example.schooltrade.api;

import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = UserSession.getToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }
        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(request);
    }
}
