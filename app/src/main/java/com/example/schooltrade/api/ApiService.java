package com.example.schooltrade.api;

import java.util.List;
import java.util.Map;

import com.example.schooltrade.entity.Conversation;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.entity.Message;
import com.example.schooltrade.entity.Transaction;
import com.example.schooltrade.entity.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ========== Auth ==========
    @POST("api/auth/login")
    Call<Result<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<Result<String>> register(@Body RegisterRequest request);

    @GET("api/user/profile")
    Call<Result<User>> getProfile();

    @PUT("api/user/profile")
    Call<Result<User>> updateProfile(@Body Map<String, String> body);

    @GET("api/user/info/{userId}")
    Call<Result<User>> getUserInfo(@Path("userId") int userId);

    // ========== Goods ==========
    @POST("api/goods")
    Call<Result<Goods>> publishGoods(@Body GoodsRequest request);

    @GET("api/goods")
    Call<Result<List<Goods>>> getAllGoods(
        @Query("category") Integer category,
        @Query("sort") String sort
    );

    @GET("api/goods/hot")
    Call<Result<List<Goods>>> getHotGoods(@Query("limit") int limit);

    @GET("api/goods/search")
    Call<Result<List<Goods>>> searchGoods(@Query("keyword") String keyword);

    @GET("api/goods/{id}")
    Call<Result<Goods>> getGoodsById(@Path("id") int goodsId);

    @GET("api/goods/my")
    Call<Result<List<Goods>>> getMyGoods(@Query("userId") int userId);

    @PUT("api/goods/{id}")
    Call<Result<String>> updateGoods(@Path("id") int goodsId, @Body GoodsRequest request);

    @DELETE("api/goods/{id}")
    Call<Result<String>> deleteGoods(@Path("id") int goodsId);

    // ========== Collect ==========
    @POST("api/collect")
    Call<Result<String>> addCollect(@Body Map<String, Integer> body);

    @HTTP(method = "DELETE", path = "api/collect", hasBody = true)
    Call<Result<String>> cancelCollect(@Body Map<String, Integer> body);

    @GET("api/collect/check")
    Call<Result<Boolean>> isCollect(@Query("userId") int userId, @Query("goodsId") int goodsId);

    @GET("api/collect/my")
    Call<Result<List<Goods>>> getMyCollect(@Query("userId") int userId);

    // ========== Message ==========
    @POST("api/message")
    Call<Result<Message>> sendMessage(@Body MessageRequest request);

    @GET("api/message/conversation")
    Call<Result<List<Message>>> getConversationMessages(
        @Query("userId") int userId,
        @Query("otherUserId") int otherUserId,
        @Query("goodsId") int goodsId
    );

    @GET("api/conversations")
    Call<Result<List<Conversation>>> getConversations(@Query("userId") int userId);

    @PUT("api/message/read")
    Call<Result<String>> markAsRead(@Body Map<String, Integer> body);

    // ========== Transaction ==========
    @POST("api/transaction")
    Call<Result<Transaction>> createTransaction(@Body TransactionRequest request);

    @GET("api/transaction/buy")
    Call<Result<List<Transaction>>> getMyBuy(@Query("userId") int userId);

    @GET("api/transaction/sell")
    Call<Result<List<Transaction>>> getMySell(@Query("userId") int userId);

    // ========== File ==========
    @Multipart
    @POST("api/files/upload")
    Call<Result<String>> uploadImage(@Part MultipartBody.Part file);
}
