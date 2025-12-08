package com.bbluecoder.sowittest.di

import android.app.Application
import androidx.room.Room
import com.bbluecoder.sowittest.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application) : AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "app_db").build()
    }

    @Singleton
    @Provides
    fun providePolygonDao(database: AppDatabase) = database.polygonDao()
}