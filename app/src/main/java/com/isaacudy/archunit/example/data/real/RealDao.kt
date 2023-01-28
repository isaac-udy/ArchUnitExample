package com.isaacudy.archunit.example.data.real

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class RealDatabaseItem(
    @PrimaryKey val id: String,
    val value: String,
    val name: String,
)

@Dao
interface RealDao {
    @Query("SELECT * FROM RealDatabaseItem")
    fun getAll(): Flow<List<RealDatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg realEntities: RealDatabaseItem)
}