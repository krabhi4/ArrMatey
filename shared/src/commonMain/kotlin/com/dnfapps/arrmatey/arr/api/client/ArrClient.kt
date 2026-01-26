package com.dnfapps.arrmatey.arr.api.client

import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrRelease
import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.CommandResponse
import com.dnfapps.arrmatey.arr.api.model.DownloadReleasePayload
import com.dnfapps.arrmatey.arr.api.model.HistoryItem
import com.dnfapps.arrmatey.arr.api.model.MonitoredResponse
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.QueuePage
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.arr.api.model.RootFolder
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.client.NetworkResult

interface ArrClient {
    suspend fun getLibrary(): NetworkResult<List<ArrMedia>>
    suspend fun getDetail(id: Long): NetworkResult<ArrMedia>
    suspend fun update(item: ArrMedia): NetworkResult<ArrMedia>
    suspend fun delete(id: Long, deleteFiles: Boolean, addImportListExclusion: Boolean): NetworkResult<Unit>
    suspend fun setMonitorStatus(id: Long, monitorStatus: Boolean): NetworkResult<List<MonitoredResponse>>
    suspend fun lookup(query: String): NetworkResult<List<ArrMedia>>
    suspend fun getQualityProfiles(): NetworkResult<List<QualityProfile>>
    suspend fun getRootFolders(): NetworkResult<List<RootFolder>>
    suspend fun getTags(): NetworkResult<List<Tag>>
    suspend fun addItemToLibrary(item: ArrMedia): NetworkResult<ArrMedia>
    suspend fun command(payload: CommandPayload): NetworkResult<CommandResponse>
    suspend fun performAutomaticSearch(id: Long): NetworkResult<CommandResponse>
    suspend fun getReleases(params: ReleaseParams): NetworkResult<List<ArrRelease>>
    suspend fun fetchActivityTasks(page: Int, pageSize: Int): NetworkResult<QueuePage>
    suspend fun getItemHistory(id: Long, page: Int, pageSize: Int): NetworkResult<List<HistoryItem>>
    suspend fun downloadRelease(payload: DownloadReleasePayload): NetworkResult<Any>
}