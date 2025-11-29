package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dnfapps.arrmatey.database.dao.InstanceDao
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstanceViewModel: ViewModel(), KoinComponent {

    private val instanceDao: InstanceDao by inject()

//    private val _instance = MutableStateFlow<Instance?>(null)
//    val instance: StateFlow<Instance> = _instance

    fun getAllInstances() = instanceDao.getAllAsFlow()

    suspend fun newInstance(instance: Instance) {
        instanceDao.insert(instance)
    }

    fun getFirstInstance(instanceType: InstanceType): Flow<Instance?> {
        return instanceDao.getFirstInstance(instanceType)
    }

}