package com.screenquest.di

import android.content.Context
import androidx.room.Room
import com.screenquest.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "screenquest.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides fun provideUsageDao(db: AppDatabase) = db.usageDao()
    @Provides fun provideGameDao(db: AppDatabase) = db.gameDao()
    @Provides fun provideAppLimitDao(db: AppDatabase) = db.appLimitDao()
}