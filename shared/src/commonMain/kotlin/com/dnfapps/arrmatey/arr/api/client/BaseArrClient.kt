package com.dnfapps.arrmatey.arr.api.client

import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.CommandResponse
import com.dnfapps.arrmatey.arr.api.model.DownloadReleasePayload
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.QueuePage
import com.dnfapps.arrmatey.arr.api.model.RootFolder
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.client.safeDelete
import com.dnfapps.arrmatey.client.safeGet
import com.dnfapps.arrmatey.client.safePost
import com.dnfapps.arrmatey.client.safePut
import com.dnfapps.arrmatey.instances.model.Instance
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.component.KoinComponent

abstract class BaseArrClient(
    protected val httpClient: HttpClient
): KoinComponent, ArrClient {
    protected abstract val instance: Instance

    protected val baseUrl: String
        get() = "${instance.url}/api/v3"

    override suspend fun getQualityProfiles(): NetworkResult<List<QualityProfile>> =
        get("qualityprofile")

    override suspend fun getRootFolders(): NetworkResult<List<RootFolder>> =
        get("rootfolder")

    override suspend fun getTags(): NetworkResult<List<Tag>> =
        get("tag")

    override suspend fun command(payload: CommandPayload): NetworkResult<CommandResponse> =
        post("command", payload)

    override suspend fun fetchActivityTasks(
        page: Int,
        pageSize: Int
    ): NetworkResult<QueuePage> =
        get<QueuePage>("queue", mapOf(
            "page" to page,
            "pageSize" to pageSize,
            "includeMovie" to true,
            "includeSeries" to true,
            "includeEpisode" to true
        )).map { it.setInstanceId(instance.id) }

    override suspend fun downloadRelease(
        payload: DownloadReleasePayload
    ): NetworkResult<Any> =
        post("release", payload)

    /**
     * Helpers
     */

    protected suspend inline fun <reified T> get(
        endpoint: String,
        params: Map<String, Any> = emptyMap()
    ): NetworkResult<T> =
        httpClient.safeGet("$baseUrl/$endpoint") {
            url {
                params.forEach { (key, value) ->
                    parameters.append(key, value.toString())
                }
            }
        }

    protected suspend inline fun <reified T, reified R> post(
        endpoint: String,
        body: T
    ): NetworkResult<R> =
        httpClient.safePost("$baseUrl/$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    protected suspend inline fun <reified T, reified R> put(
        endpoint: String,
        body: T
    ): NetworkResult<R> =
        httpClient.safePut("$baseUrl/$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    protected suspend inline fun <reified T> delete(
        endpoint: String,
        params: Map<String, Any> = emptyMap()
    ): NetworkResult<T> =
        httpClient.safeDelete("$baseUrl/$endpoint") {
            url {
                params.forEach { (key, value) ->
                    parameters.append(key, value.toString())
                }
            }
        }
}