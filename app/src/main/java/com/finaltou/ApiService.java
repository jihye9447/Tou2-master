package com.finaltou;

import com.google.gson.JsonObject;
import com.finaltou.Weather.MinutelyWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018-02-27.
 */

public interface ApiService {
    //베이스 URL

    static final String BASEURL = "https://api2.sktelecom.com/";
    static final String APPKEY = "4d3e2ebd-4960-42e1-8740-471d14a1c233";

    @GET("weather/current/hourly")
    Call<JsonObject>getHourly(@Header("appKey")String appKey, @Query("version")int version, @Query("lat")double lat,
                              @Query("lon")double lon);

    @GET("weather/current/minutely")
    Call<MinutelyWeather>getMinutely(@Header("appKey")String appKey, @Query("version")int version, @Query("lat")double lat,
                                     @Query("lon")double lon);


}