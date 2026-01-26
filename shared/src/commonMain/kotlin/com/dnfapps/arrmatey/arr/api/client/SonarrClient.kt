package com.dnfapps.arrmatey.arr.api.client

import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.CommandResponse
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.MonitoredResponse
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.arr.api.model.SeriesRelease
import com.dnfapps.arrmatey.arr.api.model.SonarrHistoryItem
import com.dnfapps.arrmatey.arr.api.model.SonarrHistoryResponse
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.instances.model.Instance
import io.ktor.client.HttpClient

class SonarrClient(
    override val instance: Instance,
    httpClient: HttpClient
) : BaseArrClient(httpClient) {

    override suspend fun getLibrary(): NetworkResult<List<ArrSeries>> =
        get("series")

    override suspend fun getDetail(id: Long): NetworkResult<ArrSeries> =
        get("series/$id")

    override suspend fun update(item: ArrMedia): NetworkResult<ArrSeries> =
        post("series/${item.id}", item)

    override suspend fun delete(
        id: Long,
        deleteFiles: Boolean,
        addImportListExclusion: Boolean
    ): NetworkResult<Unit> =
        delete(
            endpoint = "series/$id",
            params = mapOf(
                "deleteFiles" to deleteFiles,
                "addImportListExclusion" to addImportListExclusion
            )
        )

    override suspend fun lookup(query: String): NetworkResult<List<ArrSeries>> =
        get("series/lookup", mapOf("term" to query))

    override suspend fun addItemToLibrary(item: ArrMedia): NetworkResult<ArrSeries> {
        val res = post<ArrMedia, ArrSeries>("series", item)
        return res
    }

    override suspend fun getReleases(params: ReleaseParams): NetworkResult<List<SeriesRelease>> {
        if (params !is ReleaseParams.Series) {
            return NetworkResult.Error(message = "Non-series params type: $params")
        }

        val paramsMap = params.episodeId
            ?.let { epId -> mapOf("episodeId" to epId) }
            ?: buildMap<String, Any> {
                params.seriesId?.let { put("seriesId", it) }
                params.seasonNumber?.let { put("seasonNumber", it) }
            }

        return get("release", paramsMap)
    }

    override suspend fun setMonitorStatus(
        id: Long,
        monitorStatus: Boolean
    ): NetworkResult<List<MonitoredResponse>> =
        put("series/editor", mapOf(
            "monitored" to monitorStatus,
            "seriesIds" to listOf(id)
        ))

    override suspend fun getItemHistory(
        id: Long,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<SonarrHistoryItem>> =
        get<SonarrHistoryResponse>("history", mapOf(
            "page" to page,
            "pageSize" to pageSize,
            "episodeId" to id
        )).map { it.records }

    override suspend fun performAutomaticSearch(id: Long): NetworkResult<CommandResponse> =
        post("command", CommandPayload.Series(id))

    suspend fun updateEpisode(item: Episode): NetworkResult<Episode> =
        put("episode/${item.id}", item)

    suspend fun getEpisodes(
        seriesId: Long,
        seasonNumber: Int? = null,
        includeEpisodeFile: Boolean = true,
        includeImages: Boolean = true
    ): NetworkResult<List<Episode>> =
        get("episode", buildMap {
            put("seriesId", seriesId)
            seasonNumber?.let { put("seasonNumber", it) }
            if (includeEpisodeFile) put("includeEpisodeFile", true)
            if (includeImages) put("includeImages", true)
        })

}