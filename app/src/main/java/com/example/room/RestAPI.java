package com.example.room;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface RestAPI {
    @GET("users")
    Single<List<Model>> loadUsers();


}
