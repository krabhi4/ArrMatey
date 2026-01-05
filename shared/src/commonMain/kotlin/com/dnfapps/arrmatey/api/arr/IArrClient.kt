package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.model.CommandResponse
import com.dnfapps.arrmatey.api.arr.model.MonitoredResponse
import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import com.dnfapps.arrmatey.api.client.NetworkResult

interface IArrClient<T: AnyArrMedia> {

    suspend fun getLibrary(): NetworkResult<List<T>>
    suspend fun getDetail(id: Int): NetworkResult<T>
    suspend fun update(item: T): NetworkResult<T>
    suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean): NetworkResult<List<MonitoredResponse>>
    suspend fun lookup(query: String): NetworkResult<List<T>>
    suspend fun getQualityProfiles(): NetworkResult<List<QualityProfile>>
    suspend fun getRootFolders(): NetworkResult<List<RootFolder>>
    suspend fun getTags(): NetworkResult<List<Tag>>
    suspend fun addItemToLibrary(item: T): NetworkResult<T>
    suspend fun command(payload: CommandPayload): NetworkResult<CommandResponse>

}