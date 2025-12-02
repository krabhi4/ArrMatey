package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.client.NetworkResult

interface IArrClient<T> {

    suspend fun getLibrary(): NetworkResult<List<T>>

}