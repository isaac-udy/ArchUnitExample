package com.isaacudy.archunit.example.data.real

import com.isaacudy.archunit.example.domain.real.model.RealModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealRepository @Inject constructor(
    private val realApi: RealApi,
    private val realDao: RealDao,
) {

    private val allRealEntities = flow {
        syncReals()
        emitAll(realDao.getAll())
    }.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1
    )

    private suspend fun syncReals() {
        val responses = realApi.getReals()
        val realEntities = responses.map {
            RealDatabaseItem(
                id = it.value.hashCode().toString(),
                value = it.value,
                name = it.name
            )
        }
        realDao.insertAll(*realEntities.toTypedArray())
    }

    fun getAllReals(): Flow<List<RealModel>> {
        return allRealEntities.map { entities ->
            entities.map { entity ->
                RealModel(
                    id = entity.id,
                    value = BigDecimal(entity.value),
                    name = entity.name,
                )
            }
        }
    }
}