package com.mlab.knockme.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.mlab.knockme.R
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
import com.mlab.knockme.main_feature.data.repo.MainRepoImpl
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import com.mlab.knockme.main_feature.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideGoogleSignInRequest() =
//        BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                .setSupported(true)
//                // Your server's client ID, not your Android client ID.
//                .setServerClientId(Resources.getSystem().getString(R.string.knock_me_web_client_id))
//                // Only show accounts previously used to sign in.
//                .setFilterByAuthorizedAccounts(true)
//                .build()
//            )
//            .build()

//    @Provides
//    @Singleton
//    fun provideGoogleLoginAuth(context: Context): GoogleSignInClient {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestIdToken(context.getString(R.string.knock_me_web_client_id))
//            .requestId()
//            .requestProfile()
//            .build()
//        return GoogleSignIn.getClient(context, gso)
//    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(app: Application) =
        Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseDatabase(app: Application) =
        Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseStorage() =
        Firebase.storage("gs://knockme-web.appspot.com")

    @Provides
    @Singleton
    fun provideFirebaseFirestore(app: Application) =
        Firebase.firestore


    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        api: PortalApi,
        firestore: FirebaseFirestore,
        cloudStore: FirebaseStorage
    ) : AuthRepo{
        return AuthRepoImpl(auth,api,firestore,cloudStore)
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
    fun provideMainRepo(firebase: FirebaseDatabase,firestore: FirebaseFirestore, api: PortalApi): MainRepo{
        return MainRepoImpl(firebase,firestore,api)
    }

    @Provides
    @Singleton
    fun provideMainUseCases(repo: MainRepo) =
        MainUseCases(
            GetChatProfiles(repo),
            GetUserBasicInfo(repo),
            GetUserFullProfile(repo),
            GetOrCreateUserProfileInfo(repo),
            GetRandomHadith(repo),
            GetMsg(repo),
            SendMsg(repo),
            RefreshProfileInChats(repo),
            DeleteMsg(repo),
            UpdatePaymentInfo(repo),
            UpdateRegCourseInfo(repo),
            UpdateLiveResultInfo(repo),
            UpdateFullResultInfo(repo)
        )
}