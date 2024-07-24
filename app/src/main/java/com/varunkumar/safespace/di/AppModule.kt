package com.varunkumar.safespace.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.varunkumar.safespace.auth.domain.GoogleAuthClient
import com.varunkumar.safespace.shared.SharedViewModelData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleAuthClient {
        return GoogleAuthClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    @Provides
    @Singleton
    fun provideSharedViewModelData(): SharedViewModelData {
        return SharedViewModelData()
    }
}