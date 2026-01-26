package com.dnfapps.arrmatey.arr.api.client

import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.CommandResponse
import com.dnfapps.arrmatey.arr.api.model.ExtraFile
import com.dnfapps.arrmatey.arr.api.model.MonitoredResponse
import com.dnfapps.arrmatey.arr.api.model.MovieRelease
import com.dnfapps.arrmatey.arr.api.model.RadarrHistoryItem
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.instances.model.Instance
import io.ktor.client.HttpClient

class RadarrClient(
    override val instance: Instance,
    httpClient: HttpClient
): BaseArrClient(httpClient) {

    override suspend fun getLibrary(): NetworkResult<List<ArrMovie>> =
        get("movie")

    override suspend fun getDetail(id: Long): NetworkResult<ArrMovie> =
        get("movie/$id")

    override suspend fun update(item: ArrMedia): NetworkResult<ArrMovie> =
        put("movie/${item.id}", item)

    override suspend fun delete(
        id: Long,
        deleteFiles: Boolean,
        addImportListExclusion: Boolean
    ): NetworkResult<Unit> =
        delete(
            endpoint = "movie/$id",
            params = mapOf(
                "deleteFiles" to deleteFiles,
                "addImportListExclusion" to addImportListExclusion
            )
        )

    override suspend fun setMonitorStatus(
        id: Long,
        monitorStatus: Boolean
    ): NetworkResult<List<MonitoredResponse>> =
        put("movie/editor", mapOf(
            "monitored" to monitorStatus,
            "movieIds" to listOf(id)
        ))

    override suspend fun lookup(query: String): NetworkResult<List<ArrMovie>> =
        get("movie/lookup", mapOf("term" to query))

    override suspend fun addItemToLibrary(item: ArrMedia): NetworkResult<ArrMovie> =
        post("movie", item)

    override suspend fun getReleases(params: ReleaseParams): NetworkResult<List<MovieRelease>> {
        if (params !is ReleaseParams.Movie) {
            return NetworkResult.Error(message = "Non-movie params type: $params")
        }
        return get("release", mapOf("movieId" to params.movieId))
    }

    override suspend fun getItemHistory(
        id: Long,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<RadarrHistoryItem>> =
        get("history/movie", mapOf(
            "page" to page,
            "pageSize" to pageSize,
            "movieId" to id
        ))

    override suspend fun performAutomaticSearch(id: Long): NetworkResult<CommandResponse> =
        post("command", CommandPayload.Movie(listOf(id)))

    suspend fun getMovieExtraFile(id: Long): NetworkResult<List<ExtraFile>> =
        get("extrafile", mapOf("movieId" to id))

}