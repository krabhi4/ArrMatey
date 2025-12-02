package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.call.body
import io.ktor.client.request.get

class SonarrClient(instance: Instance) : BaseArrClient(instance), IArrClient<ArrSeries> {

    override suspend fun getLibrary(): NetworkResult<List<ArrSeries>> {
        val resp = httpClient.safeGet<List<ArrSeries>>("api/v3/series")
        return resp
    }

    suspend fun getSeriesEpisodes(seriesId: Int): List<Any> {
        val resp: List<Any> = httpClient.get("api/v3/episode?seriesId=${seriesId}").body()
        return resp
    }

    suspend fun getSeriesEpisodeFiles(seriesId: Int): List<Any> {
        val resp: List<Any> = httpClient.get("api/v3/episodeFile?seriesId=${seriesId}").body()
        return resp
    }

}