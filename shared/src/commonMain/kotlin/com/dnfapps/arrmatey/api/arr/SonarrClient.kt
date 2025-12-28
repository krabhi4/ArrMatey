package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.Episode
import com.dnfapps.arrmatey.api.arr.model.MonitoredResponse
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import com.dnfapps.arrmatey.api.client.safePost
import com.dnfapps.arrmatey.api.client.safePut
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray

class SonarrClient(instance: Instance) : BaseArrClient<ArrSeries>(instance) {

    override suspend fun getLibrary(): NetworkResult<List<ArrSeries>> {
        val resp = httpClient.safeGet<List<ArrSeries>>("api/v3/series")
        return resp
    }

    override suspend fun getDetail(id: Int): NetworkResult<ArrSeries> {
        val resp = httpClient.safeGet<ArrSeries>("api/v3/series/$id")
        return resp
    }

    override suspend fun update(item: ArrSeries): NetworkResult<ArrSeries> {
        val resp = httpClient.safePut<ArrSeries>("api/v3/series/${item.id}") {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return resp
    }

    override suspend fun lookup(query: String): NetworkResult<List<ArrSeries>> {
        val resp = httpClient.safeGet<List<ArrSeries>>("api/v3/series/lookup?term=$query")
        return resp
    }

    override suspend fun addItemToLibrary(item: ArrSeries): NetworkResult<ArrSeries> {
        val resp = httpClient.safePost<ArrSeries>("api/v3/series") {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return resp
    }

    suspend fun updateEpisode(item: Episode): NetworkResult<Episode> {
        val resp = httpClient.safePut<Episode>("api/v3/episode/${item.id}") {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return resp
    }

    suspend fun getEpisodes(
        seriesId: Int,
        seasonNumber: Int? = null,
        includeEpisodeFile: Boolean = true
    ): NetworkResult<List<Episode>> {
        val queryParams = listOfNotNull(
            "seriesId=$seriesId",
            seasonNumber?.let { "seasonNumber=$it" },
            if (includeEpisodeFile) "includeEpisodeFile=true" else null
        ).joinToString("&")
        val query = "?$queryParams"
        val resp = httpClient.safeGet<List<Episode>>("api/v3/episode$query")
        return resp
    }

    override suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean): NetworkResult<List<MonitoredResponse>> {
        val resp = httpClient.safePut<List<MonitoredResponse>>("api/v3/series/editor") {
            contentType(ContentType.Application.Json)

            val body = buildJsonObject {
                put("monitored", JsonPrimitive(monitorStatus))
                putJsonArray("seriesIds") {
                    add(JsonPrimitive(id))
                }
            }
            setBody(body)
        }
        return resp
    }

}