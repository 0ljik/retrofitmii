package com.exmi.retrofitmii;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface JsonPlaceholderApi {
    /*@GET("posts")
    Call<List<Post>> getPosts(
            @Query("userId") Integer[] userId,
            //@Query("userId") Integer userId2,
            @Query("_sort") String sort,
            @Query("_order") String order);

    @GET("posts")
    Call<List<Post>> getPosts(@QueryMap Map<String, String> parameters);

    @GET("posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") int postId);

    @GET
    Call<List<Comment>> getComments(@Url String url);

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @FormUrlEncoded
    @POST("api")
    Call<Post> createPost(
            @Field("userId") int userId,
            @Field("title") String title,
            @Field("body") String text
    );

    @POST("api")
    Call<Post> djantes(@Body Post post);*/

    @POST("api-token-auth")
    Call<Token> signin(@Body Login signin);

    @POST("signup")
    Call<Signup> signup(@Body Signup signup);

    @POST("create")
    Call<ResponseBody> create(
            @Header("Authorization")  String djangoToken,
            @Body JsonObject json
    );

    @POST("accept")
    Call<ResponseBody> accept(
            @Header("Authorization")  String djangoToken,
            @Body JsonObject json
    );

    @POST("check")
    Call<ResponseBody> check(
            @Header("Authorization")  String djangoToken,
            @Body JsonObject json
    );
}
