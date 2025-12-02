package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import com.dnfapps.arrmatey.model.Instance

class RadarrClient(instance: Instance): BaseArrClient(instance), IArrClient<ArrMovie> {

    override suspend fun getLibrary(): NetworkResult<List<ArrMovie>> {
        val resp = httpClient.safeGet<List<ArrMovie>>("api/v3/movie")
        return resp
    }

}