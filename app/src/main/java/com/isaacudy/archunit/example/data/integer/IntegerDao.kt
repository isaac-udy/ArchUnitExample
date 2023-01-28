package com.isaacudy.archunit.example.data.integer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class IntegerEntity(
    @PrimaryKey val id: String,
    val value: String,
)

@Dao
interface IntegerDao {
    @Query("SELECT * FROM IntegerEntity")
    fun getAll(): Flow<List<IntegerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg integerEntities: IntegerEntity)
}