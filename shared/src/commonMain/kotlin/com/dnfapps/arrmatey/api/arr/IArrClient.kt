package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.MonitoredResponse
import com.dnfapps.arrmatey.api.client.NetworkResult

interface IArrClient<T: AnyArrMedia> {

    suspend fun getLibrary(): NetworkResult<List<T>>
    suspend fun getDetail(id: Int): NetworkResult<T>
    suspend fun update(item: T): NetworkResult<T>
    suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean): NetworkResult<List<MonitoredResponse>>

}