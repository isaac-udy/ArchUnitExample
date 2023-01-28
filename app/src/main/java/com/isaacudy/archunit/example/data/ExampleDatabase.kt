package com.isaacudy.archunit.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isaacudy.archunit.example.data.integer.IntegerDao
import com.isaacudy.archunit.example.data.integer.IntegerEntity
import com.isaacudy.archunit.example.data.real.RealDao
import com.isaacudy.archunit.example.data.real.RealDatabaseItem
import com.isaacudy.archunit.example.data.user.UserDao
import com.isaacudy.archunit.example.data.user.UserEntity
import com.isaacudy.archunit.example.data.user.UserFavoriteEntity

@Database(
    entities = [
        IntegerEntity::class,
        RealDatabaseItem::class,
        UserEntity::class,
        UserFavoriteEntity::class
    ],
    version = 1,
)
abstract class ExampleDatabase : RoomDatabase() {
    abstract fun getIntegerDao(): IntegerDao
    abstract fun getRealDao(): RealDao
    abstract fun getUserDao(): UserDao
}