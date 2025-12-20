package com.dnfapps.arrmatey.database

import com.dnfapps.arrmatey.database.dao.ConflictField
import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.database.dao.InstanceDao
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstanceRepository: KoinComponent {

    private val instanceDao: InstanceDao by inject()

    val allInstances = instanceDao.observeAllInstances()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private suspend fun newInstance(instance: Instance): Long {
        val shouldBeSelected = allInstances.value
                .none { i ->
                    i.type == instance.type && i.selected
                }
        val newInstance = instance.copy(selected = shouldBeSelected)
        return instanceDao.insert(newInstance)
    }

    suspend fun createInstance(instance: Instance): InsertResult {
        return try {
            val urlConflict = instanceDao.findByUrl(instance.url) != null
            val labelConflict = instanceDao.findByLabel(instance.label) != null

            val conflictFields = buildList {
                if (urlConflict) add(ConflictField.InstanceUrl)
                if (labelConflict) add(ConflictField.InstanceLabel)
            }

            if (conflictFields.isNotEmpty()) {
                InsertResult.Conflict(fields = conflictFields)
            } else {
                val id = newInstance(instance)
                if (id > 0L) InsertResult.Success(id)
                else InsertResult.Error("Failed to save")
            }

        } catch (e: Exception) {
            InsertResult.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun updateInstance(instance: Instance): InsertResult {
        return try {
            val urlConflict = instanceDao.findOtherByUrl(instance.url, instance.id) != null
            val labelConflict = instanceDao.findOtherByLabel(instance.label, instance.id) != null

            val conflictField = buildList {
                if (urlConflict) add(ConflictField.InstanceUrl)
                if (labelConflict) add(ConflictField.InstanceLabel)
            }

            if (conflictField.isNotEmpty()) {
                InsertResult.Conflict(fields = conflictField)
            } else {
                val rows = instanceDao.update(instance)
                if (rows > 0) InsertResult.Success(instance.id)
                else InsertResult.Error("Failed to update")
            }
        } catch (e: Exception) {
            InsertResult.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun deleteInstance(instance: Instance) {
        instanceDao.deleteAndUpdateSelected(instance)
    }

    suspend fun setInstanceActive(instance: Instance) {
        instanceDao.setInstanceAsSelected(instance.id, instance.type)
    }

}