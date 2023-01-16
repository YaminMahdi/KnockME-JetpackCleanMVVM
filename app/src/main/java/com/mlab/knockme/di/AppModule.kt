package com.mlab.knockme.di

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.data.repo.AuthRepoImpl
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import com.mlab.knockme.auth_feature.domain.use_cases.AuthUseCases
import com.mlab.knockme.auth_feature.domain.use_cases.FacebookLogin
import com.mlab.knockme.auth_feature.domain.use_cases.FirebaseAuthState
import com.mlab.knockme.auth_feature.domain.use_cases.FirebaseSignIn
import com.mlab.knockme.auth_feature.domain.use_cases.FirebaseSignOut
import com.mlab.knockme.auth_feature.domain.use_cases.GetStudentIdInfo
import com.mlab.knockme.auth_feature.domain.use_cases.GetStudentInfo
import com.mlab.knockme.auth_feature.domain.use_cases.IsUserAuthenticated
import com.mlab.knockme.main_feature.data.repo.MsgRepoImpl
import com.mlab.knockme.main_feature.domain.repo.MsgRepo
import com.mlab.knockme.main_feature.domain.use_case.DeleteMsg
import com.mlab.knockme.main_feature.domain.use_case.GetMsg
import com.mlab.knockme.main_feature.domain.use_case.MsgUseCases
import com.mlab.knockme.main_feature.domain.use_case.SendMsg
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(app: Application) =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(app: Application) =
        Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseFirestore(app: Application) =
        Firebase.firestore


    @Provides
    @Singleton
    fun provideAuthRepo(auth: FirebaseAuth, api: PortalApi, firestore: FirebaseFirestore) : AuthRepo{
        return AuthRepoImpl(auth,api,firestore)
    }

    @Provides
    @Singleton
    fun providePortalApi(): PortalApi =
        Retrofit.Builder()
            .baseUrl(PortalApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PortalApi::class.java)

    @Provides
    @Singleton
    fun provideAuthUseCases(repo: AuthRepo) =
        AuthUseCases(
            IsUserAuthenticated(repo),
            FirebaseAuthState(repo),
            FirebaseSignIn(repo),
            FacebookLogin(repo),
            FirebaseSignOut(repo),
            GetStudentIdInfo(repo),
            GetStudentInfo(repo)
        )

    @Provides
    @Singleton
    fun provideMsgRepo(db: FirebaseDatabase): MsgRepo{
        return MsgRepoImpl(db)
    }

    @Provides
    @Singleton
    fun provideMsgUseCases(repo: MsgRepo) =
        MsgUseCases(
            GetMsg(repo),
            SendMsg(repo),
            DeleteMsg(repo)
        )
}