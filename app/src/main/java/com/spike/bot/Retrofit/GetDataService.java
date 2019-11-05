package com.spike.bot.Retrofit;

import com.spike.bot.model.LockInitResultObj;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface GetDataService {

    @Headers({"Content-Type: application/json"})
    @GET
    Call<ResponseBody> saveIrBlaster(@Url String url);



    @POST("/v3/lock/initialize")
    @FormUrlEncoded
    Call<ResponseBody> lockInit(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockData") String lockData, @Field("lockAlias") String alias, @Field("date") long date);


    @POST("/oauth2/token")
    @FormUrlEncoded
    Call<String> auth(@Field("client_id") String clientId, @Field("client_secret") String clientSecret, @Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password, @Field("redirect_uri") String redirectUri);


    @POST("/v3/lock/list")
    @FormUrlEncoded
    Call<String> getLockList(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("date") long date);



    @GET("/v3/key/list")
    Call<ResponseBody> getUserKeyList(@QueryMap Map<String, String> params);

    @POST("/v3/lock/unlock")
    @FormUrlEncoded
    Call<ResponseBody> unlockGatewayUse(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockId") int pageSize, @Field("date") long date);

    @POST("/v3/lock/detail")
    @FormUrlEncoded
    Call<String> lockDetails(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockId") int pageSize, @Field("date") long date);

    @POST("/v3/lock/lock")
    @FormUrlEncoded
    Call<ResponseBody> lockGatewayUse(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockId") int pageSize, @Field("date") long date);


    @POST("/v3/lock/queryStatus")
    @FormUrlEncoded
    Call<String> lockStatus(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockId") int pageSize, @Field("date") long date);

    @POST("/v3/lock/delete")
    @FormUrlEncoded
    Call<String> lockdelete(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("lockId") int pageSize, @Field("date") long date);


    @POST("/v3/user/getUid")
    @FormUrlEncoded
    Call<String> addGateway(@Field("clientId") String clientId, @Field("accessToken") String accessToken,@Field("date") long date);

  @POST("/v3/gateway/isInitSuccess")
    @FormUrlEncoded
    Call<String> isInitSuccess(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("gatewayNetMac") String gatewayNetMac,@Field("date") long date);


    @POST("/v3/gateway/uploadDetail")
    @FormUrlEncoded
    Call<String> uploadGatewayDetail(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("gatewayId") int gatewayId, @Field("modelNum") String modelNum, @Field("hardwareRevision") String hardwareRevision, @Field("firmwareRevision") String firmwareRevision, @Field("networkName") String networkName, @Field("date") long date);


    @POST("/v3/gateway/isInitSuccess")
    @FormUrlEncoded
    Call<String> gatewayIsInitSuccess(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("gatewayNetMac") String gatewayNetMac, @Field("date") long date);

    @POST("/v3/gateway/list")
    @FormUrlEncoded
    Call<String> gatewaylist(@Field("clientId") String clientId, @Field("accessToken") String accessToken, @Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("date") long date);


    //date
    @POST("/v3/gateway/list")
    @FormUrlEncoded
    Call<String> signTTlock(@Field("clientId") String clientId, @Field("clientSecret") String accessToken, @Field("username") int pageNo, @Field("password") int pageSize, @Field("date") long date);


    @POST("/v3/gateway/listLock")
    @FormUrlEncoded
    Call<String> getlockbygateway(@Field("clientId") String clientId, @Field("accessToken") String accessToken,  @Field("gatewayId") String pageSize, @Field("date") long date);

    //  "bridge_id": ,
    //         "bridge_name": ,
    @POST
    @FormUrlEncoded
    Call<String> bridgeUploadtoServer(@Url String url, @Field("bridge_id") String bridge_id, @Field("bridge_name") String bridge_name);


    @GET
    Call<String> bridgelist(@Url String url);


    //clientId	String	Y	The app_id which is assigned by system when you create an application
    //clientSecret	String	Y	The app_secret which is assigned by system when you create an application
    //username	String	Y	User name
    //password	String	Y	Password(32 chars, low case, md5 encrypted)
    @POST("/v3/user/register")
    @FormUrlEncoded
    Call<String> registerUser(@Field("clientId") String clientId, @Field("clientSecret") String accessToken, @Field("username") String gatewayNetMac, @Field("password") String password, @Field("date") long date);



}