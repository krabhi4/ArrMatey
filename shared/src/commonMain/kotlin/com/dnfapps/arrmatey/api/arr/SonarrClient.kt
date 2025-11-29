package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.call.body
import io.ktor.client.request.get

class SonarrClient(instance: Instance) : BaseArrClient(instance) {

    suspend fun getLibrary(): List<ArrSeries> {
        val resp: List<ArrSeries> = httpClient.get("api/v3/series").body()
        return resp
    }

}