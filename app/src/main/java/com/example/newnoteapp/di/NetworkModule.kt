package com.example.newnoteapp.di

import com.example.newnoteapp.data.remote.NetworkConfig
import com.example.newnoteapp.data.remote.TodoRemoteDataSource
import com.example.newnoteapp.data.remote.api.TodoApi
import com.example.newnoteapp.data.remote.RemoteDataSource
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    single<Interceptor>(qualifier = org.koin.core.qualifier.named("auth")) {
        Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${NetworkConfig.BEARER_TOKEN}")

            if (NetworkConfig.ENABLE_ERROR_GENERATION) {
                requestBuilder.addHeader("X-Generate-Fails", NetworkConfig.ERROR_THRESHOLD.toString())
            }

            chain.proceed(requestBuilder.build())
        }
    }

    single<Interceptor>(qualifier = org.koin.core.qualifier.named("logging")) {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single<OkHttpClient> {
        val authInterceptor: Interceptor = get(qualifier = org.koin.core.qualifier.named("auth"))
        val loggingInterceptor: Interceptor = get(qualifier = org.koin.core.qualifier.named("logging"))

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<TodoApi> {
        get<Retrofit>().create(TodoApi::class.java)
    }

    single<RemoteDataSource> {
        TodoRemoteDataSource(get<TodoApi>())
    }
}
