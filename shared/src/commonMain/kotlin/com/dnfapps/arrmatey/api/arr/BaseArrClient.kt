package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.model.CommandResponse
import com.dnfapps.arrmatey.api.arr.model.DownloadReleasePayload
import com.dnfapps.arrmatey.api.arr.model.IArrRelease
import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.QueuePage
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import com.dnfapps.arrmatey.api.client.safePost
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BaseArrClient<T: AnyArrMedia, R: IArrRelease, P: ReleaseParams>(
    instance: Instance
): KoinComponent, IArrClient<T, R, P> {
    protected val httpClient: HttpClient by inject { parametersOf(instance) }

    override suspend fun getQualityProfiles(): NetworkResult<List<QualityProfile>> {
        val resp = httpClient.safeGet<List<QualityProfile>>("api/v3/qualityprofile")
        return resp
    }

    override suspend fun getRootFolders(): NetworkResult<List<RootFolder>> {
        val resp = httpClient.safeGet<List<RootFolder>>("api/v3/rootfolder")
        return resp
    }

    override suspend fun getTags(): NetworkResult<List<Tag>> {
        val resp = httpClient.safeGet<List<Tag>>("api/v3/tag")
        return resp
    }

    override suspend fun command(payload: CommandPayload): NetworkResult<CommandResponse> {
        val resp = httpClient.safePost<CommandResponse>("api/v3/command") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return resp
    }

    override suspend fun fetchActivityTasks(instanceId: Long, pageSize: Int): NetworkResult<QueuePage> {
        val query = "?pageSize=$pageSize&includeMovie=true&includeSeries=true&includeEpisode=true"
        val resp = httpClient.safeGet<QueuePage>("api/v3/queue$query")
        if (resp is NetworkResult.Success) {
            resp.data.records.forEach { it.instanceId = instanceId }
        }
        return resp
    }

    suspend fun downloadRelease(payload: DownloadReleasePayload): NetworkResult<Any> {
        val resp = httpClient.safePost<Any>("api/v3/release") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return resp
    }
}