package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.call.body
import io.ktor.client.request.get

class RadarrClient(instance: Instance): BaseArrClient(instance) {

    suspend fun getLibrary(): List<ArrMovie> {
        val resp: List<ArrMovie> = httpClient.get("api/v3/movie").body()
        return resp
    }

}