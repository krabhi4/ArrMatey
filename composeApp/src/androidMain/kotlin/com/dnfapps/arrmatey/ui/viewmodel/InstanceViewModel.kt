package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dnfapps.arrmatey.database.dao.InstanceDao
import com.dnfapps.arrmatey.model.Instance
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstanceViewModel: ViewModel(), KoinComponent {

    private val instanceDao: InstanceDao by inject()

    fun getAllInstances() = instanceDao.getAllAsFlow()

    suspend fun newInstance(instance: Instance) {
        instanceDao.insert(instance)
    }

}