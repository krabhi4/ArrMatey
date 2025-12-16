package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class RadarrRepository(instance: Instance): BaseArrRepository<ArrMovie>(instance) {

    override val client: RadarrClient by inject { parametersOf(instance) }

    init {
        if (instance.type != InstanceType.Radarr) {
            throw IllegalArgumentException("Cannot instantiate RadarrViewModel with an instance of type ${instance.type}")
        }
    }

}