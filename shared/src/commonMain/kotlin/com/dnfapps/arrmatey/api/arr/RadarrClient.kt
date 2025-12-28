package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrMovie
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

class RadarrClient(instance: Instance): BaseArrClient<ArrMovie>(instance) {

    override suspend fun getLibrary(): NetworkResult<List<ArrMovie>> {
        val resp = httpClient.safeGet<List<ArrMovie>>("api/v3/movie")
        return resp
    }

    override suspend fun getDetail(id: Int): NetworkResult<ArrMovie> {
        val resp = httpClient.safeGet<ArrMovie>("api/v3/movie/$id")
        return resp
    }

    override suspend fun update(item: ArrMovie): NetworkResult<ArrMovie> {
        val resp = httpClient.safePut<ArrMovie>("api/v3/move/${item.id}") {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return resp
    }

    override suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean): NetworkResult<List<MonitoredResponse>> {
        val resp = httpClient.safePut<List<MonitoredResponse>>("api/v3/movie/editor") {
            contentType(ContentType.Application.Json)

            val body = buildJsonObject {
                put("monitored", JsonPrimitive(monitorStatus))
                putJsonArray("movieIds") {
                    add(JsonPrimitive(id))
                }
            }
            setBody(body)
        }
        return resp
    }

    override suspend fun lookup(query: String): NetworkResult<List<ArrMovie>> {
        val resp = httpClient.safeGet<List<ArrMovie>>("api/v3/movie/lookup?term=$query")
        return resp
    }

    override suspend fun addItemToLibrary(item: ArrMovie): NetworkResult<ArrMovie> {
        val resp = httpClient.safePost<ArrMovie>("api/v3/movie") {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        return resp
    }

}