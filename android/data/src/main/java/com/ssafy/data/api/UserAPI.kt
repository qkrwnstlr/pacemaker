package com.ssafy.data.api

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.dto.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAPI {

    @POST("users/{uid}")//todo  name string말고 dto에 넣어
    suspend fun signUp(
        @Path("uid") uid: String,
        @Body name: LoginRequestBody
    ): Response<LoginResponseBody>

    @PUT("users")
    suspend fun modify(@Body user: User): Response<User>

    @DELETE("users")
    suspend fun delete(@Body user: User): Response<Unit>

    @GET("users/{uid}")
    suspend fun getInfo(@Path("uid") uid: String): Response<User>

    @GET("users/{uid}/coach")
    suspend fun getCoach(@Path("uid") uid: String): Response<Coach>

    @PUT("users/{uid}/coach")
    suspend fun setCoach(@Path("uid") uid: String, @Body coach: Coach): Response<Unit>

}
