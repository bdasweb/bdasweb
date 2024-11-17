package com.upipay.app.data.network.di




import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.upipay.app.BuildConfig
import com.upipay.app.data.network.Config.BASE_URL
import com.upipay.app.data.network.RetroApi
import com.upipay.app.ui.base.PayConApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val retryInterceptor = Interceptor { chain ->
            var request = chain.request()
            var response: Response? = null
            var error: IOException? = null
            var retryCount = 0
            val maxRetries = 3 // Maximum number of retries
            val retryDelayMillis = 1000L // Delay between retries in milliseconds

            while (response == null && retryCount < maxRetries) {
                try {
                    response = chain.proceed(request)
                } catch (e: IOException) {
                    error = e
                    retryCount++
                    Thread.sleep(retryDelayMillis) // Add a delay before retrying
                }
            }

            if (response != null) {
                response
            } else {
                throw error ?: IOException("Unknown error")
            }
        }
        val chuckInterceptor = ChuckerInterceptor.Builder(PayConApplication.instance.contextReturn())
            .collector(ChuckerCollector(PayConApplication.instance.contextReturn()))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
        val okHttpClientBuilder =  OkHttpClient.Builder()

            .addInterceptor(retryInterceptor)
            //.addInterceptor(chuckInterceptor)
            .addInterceptor(ResponseCodeInterceptor())
            .callTimeout(900, TimeUnit.SECONDS)
            .connectTimeout(900, TimeUnit.SECONDS)
            .readTimeout(900, TimeUnit.SECONDS)
            .writeTimeout(900, TimeUnit.SECONDS)
            /*.connectionPool(ConnectionPool(5, 5L, TimeUnit.MINUTES))*/
        // Conditionally add ChuckerInterceptor in debug mode
        //okHttpClientBuilder .addInterceptor(loggingInterceptor)
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(chuckInterceptor)
            okHttpClientBuilder .addInterceptor(loggingInterceptor)
           // okHttpClientBuilder.addInterceptor(TimingInterceptor())
           // okHttpClientBuilder.addInterceptor(GzipRequestInterceptor())
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): RetroApi {
        return retrofit.create(RetroApi::class.java)
    }
}

class ResponseCodeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
       /* if (originalResponse.code == 205) {
            return originalResponse.newBuilder()
                .code(200)
                .build()
        }*/
        return originalResponse
    }
}