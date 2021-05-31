package com.example.musicandfriends;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @GET("/user_auth/{email}/{pass}/")
    Call<Integer> userAuth(@Path("email") String email, @Path("pass") String pass);

    @POST("/user_reg/{name}/{email}/{pass}/{city}/")
    Call<Integer> userReg(@Path("name") String name, @Path("email") String email, @Path("pass") String pass, @Path("city") String city);

    @GET("/profilepage/{ID}/")
    Call<User> getProfile(@Path("ID") int ID);

    @GET("/searchpage/{ID}/{count}/{params}/")
    Call<User[]> getSearchPageContent(@Path("ID") int ID, @Path("count") int count, @Path("params") String params);

    @GET("/avatars/{ID}/")
    Call<ResponseBody> downloadAvatar(@Path("ID") int ID);

    @POST("/friends_req_add/{myID}/{ID}/")
    Call<Boolean> friendsReqAdd(@Path("myID") int myID, @Path("ID") int ID);

    @POST("/friends_del/{myID}/{ID}/")
    Call<Boolean> friendsDel(@Path("myID") int myID, @Path("ID") int ID);

    @GET("/get_status/{myID}/{ID}/")
    Call<Integer> getStatus(@Path("myID") int myID, @Path("ID") int ID);

    @POST("/set_music_prefs")
    Call<String> setMusicPrefs(@Query("ID") int ID, @Query("music_prefs") boolean[] musicPrefs);

    @Multipart
    @POST("/set_avatar/{ID}/")
    Call<String> setAvatar(@Part MultipartBody.Part mbp, @Path("ID") int ID);
}
