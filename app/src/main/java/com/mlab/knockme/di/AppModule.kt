package com.mlab.knockme.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.mlab.knockme.BuildConfig
import com.mlab.knockme.auth_feature.data.data_source.NullOnEmptyConverterFactory
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.data.repo.AuthRepoImpl
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import com.mlab.knockme.core.util.Constants
import com.mlab.knockme.main_feature.data.repo.MainRepoImpl
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() =
        Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseDatabase() =
        Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseStorage() =
        Firebase.storage("gs://knockme-web.appspot.com")

    @Provides
    @Singleton
    fun provideFirebaseFirestore() =
        Firebase.firestore


    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        api: PortalApi,
        firestore: FirebaseFirestore,
        cloudStore: FirebaseStorage
    ): AuthRepo {
        return AuthRepoImpl(auth, api, firestore, cloudStore)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val chuckerInterceptor =
            ChuckerInterceptor.Builder(context).apply {
                maxContentLength(10000)
            }.build()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .apply {
                if (BuildConfig.DEBUG){
                    addInterceptor(loggingInterceptor)
                    addInterceptor(chuckerInterceptor)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun providePortalApi(okHttpClient: OkHttpClient): PortalApi =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL_NEW)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PortalApi::class.java)


    @Provides
    @Singleton
    fun provideMainRepo(
        firebase: FirebaseDatabase,
        firestore: FirebaseFirestore,
        api: PortalApi
    ): MainRepo {
        return MainRepoImpl(firebase, firestore, api)
    }
}
