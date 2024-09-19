package com.ssafy.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.ssafy.data.BuildConfig
import com.ssafy.data.api.PlanAPI
import com.ssafy.data.api.TrainAPI
import com.ssafy.data.api.UserAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIME_OUT = 25000L

    @Singleton
    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Singleton
    @Provides
    fun provideNullConvertFactory(): Converter.Factory = object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
            return Converter<ResponseBody, Any> { body ->
                if (body.contentLength() == 0L) null
                else delegate.convert(body)
            }
        }
    }

    @Singleton
    @Provides
    fun provideUserRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        nullConverterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(nullConverterFactory)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideUserAPI(retrofit: Retrofit): UserAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideLectureAPI(retrofit: Retrofit): PlanAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideInfoAPI(retrofit: Retrofit): TrainAPI = retrofit.create()

}