package com.example.hogfeed.Model;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface
{
    @GET("/events/all")
    Call<List<Event>> getEvents();

    @GET("/events/{id}")
    Call<Event> getEvent(@Path("id") int id);

    @POST("/events/new")
    Call<Event> postEvent(@Body Event event);

    @GET("/events/delete/{id}")
    Call<Event> deleteEvent(@Path("id") int id);


}
