package com.ssafy.data.api

import com.ssafy.data.response.ApiResponse
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAPI {

    @POST("users")
    suspend fun signUp(@Body user: User): Response<ApiResponse<Unit>>

    @PUT("users")
    suspend fun modify(@Body user: User): Response<ApiResponse<User>>

    @DELETE("users")
    suspend fun delete(@Body user: User): Response<ApiResponse<Unit>>

    @GET("users/{uid}")
    suspend fun getInfo(@Path("uid") uid: String): Response<ApiResponse<User>>

    @GET("users/{uid}/coach")
    suspend fun getCoach(@Path("uid") uid: String): Response<ApiResponse<Coach>>

    @PUT("users/{uid}/coach")
    suspend fun setCoach(@Path("uid") uid: String, @Body coach: Coach): Response<ApiResponse<Unit>>

}
