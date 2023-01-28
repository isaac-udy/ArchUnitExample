package com.isaacudy.archunit.example.data

import android.app.Application
import androidx.room.Room
import com.isaacudy.archunit.example.data.integer.IntegerDao
import com.isaacudy.archunit.example.data.real.RealDao
import com.isaacudy.archunit.example.data.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): ExampleDatabase {
        return Room.databaseBuilder(
            application,
            ExampleDatabase::class.java, "database-name"
        ).build()
    }

    @Provides
    @Singleton
    fun provideIntegerDao(database: ExampleDatabase): IntegerDao = database.getIntegerDao()

    @Provides
    @Singleton
    fun provideRealDao(database: ExampleDatabase): RealDao = database.getRealDao()

    @Provides
    @Singleton
    fun provideUserDao(database: ExampleDatabase): UserDao = database.getUserDao()
}