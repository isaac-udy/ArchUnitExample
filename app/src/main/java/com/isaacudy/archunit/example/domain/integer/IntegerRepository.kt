package com.isaacudy.archunit.example.domain.integer

import android.util.Log
import com.isaacudy.archunit.example.data.integer.IntegerApi
import com.isaacudy.archunit.example.data.integer.IntegerDao
import com.isaacudy.archunit.example.data.integer.IntegerEntity
import com.isaacudy.archunit.example.domain.integer.model.IntegerModel
import com.isaacudy.archunit.example.domain.real.GetReals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntegerRepository @Inject constructor(
    private val integerApi: IntegerApi,
    private val integerDao: IntegerDao,
    private val getReals: GetReals
) {

    private val allIntegerEntities = flow {
        syncIntegers()
        emitAll(integerDao.getAll())
    }.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1
    )

    private suspend fun syncIntegers() {
        val integerResponses = integerApi.getIntegers()
        val integerEntities = integerResponses.map {
            IntegerEntity(
                id = it.value.hashCode().toString(),
                value = it.value,
            )
        }
        integerDao.insertAll(*integerEntities.toTypedArray())
    }

    fun getAllIntegers(): Flow<List<IntegerModel>> {
        return allIntegerEntities.map { entities ->

            getReals().first()
                .map { it.value.toBigInteger() }
                .forEach {
                    Log.d("Debugging", "Found Real with Integer value of $it, should we do something special for this?")
                }

            entities.map { entity ->
                IntegerModel(
                    id = entity.id,
                    value = BigInteger(entity.value),
                )
            }
        }
    }
}