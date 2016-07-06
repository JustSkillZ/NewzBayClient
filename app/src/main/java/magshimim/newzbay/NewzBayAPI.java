package magshimim.newzbay;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NewzBayAPI
{
    @GET("/IP")
    Call<InfoFromServer> getIP();

    @POST("/authenticate")
    Call<AuthenticationRecieve> authenticateUser(@Body AuthenticationSend authenticationSend);

    @GET("/subWeb")
    Call<SubWeb> getAllRSS(@Header ("x-access-token") String token);

    @POST("/priority")
    Call<InfoFromServer> setPriority(@Header ("x-access-token") String token, @Body ArrayList<Priority> priorityList);

    @FormUrlEncoded
    @POST ("/deletePriority")
    Call<InfoFromServer> deletePrioritySubject(@Header ("x-access-token") String token, @Field("subject") String subject);

    @FormUrlEncoded
    @POST("/userPriority")
    Call<UserPriority> getUserPriority(@Header ("x-access-token") String token, @Field("subject") String subject);

    @FormUrlEncoded
    @POST("/article/getArticle")
    Call<JsonRecievedArticles> getArticles(@Header ("x-access-token") String token, @Field("subject") String subject);

    @FormUrlEncoded
    @POST("/article/like")
    Call<InfoFromServer> like(@Header ("x-access-token") String token, @Field("URL") String articleURL);

    @FormUrlEncoded
    @POST("/article/comment/addComment")
    Call<InfoFromServer> addComment(@Header ("x-access-token") String token, @Field("URL") String articleURL, @Field("comment") String comment);

    @FormUrlEncoded
    @POST("/article/comment/deleteComment")
    Call<InfoFromServer> deleteComment(@Header ("x-access-token") String token, @Field("URL") String articleURL, @Field("ID") String id);

    @FormUrlEncoded
    @POST("/article/comment/getComments")
    Call<JsonRecievedComments> getComments(@Header ("x-access-token") String token, @Field("URL") String articleURL);

    @GET("/article/getHotnews")
    Call<JsonRecievedArticles> getHotNews(@Header ("x-access-token") String token);

    @FormUrlEncoded
    @POST("/article/getMoreArticles")
    Call<JsonRecievedArticles> getMoreArticles(@Header ("x-access-token") String token, @Field("subject") String subject, @Field("URL") String lastArticleURL);
}
