package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class HistoryEventType {
    Unknown,

    @SerialName("grabbed")
    Grabbed,

    @SerialName("downloadFolderImported")
    DownloadFolderImported,

    @SerialName("downloadFailed")
    DownloadFailed,

    @SerialName("downloadIgnored")
    DownloadIgnored,

    @SerialName("movieFileRenamed")
    MovieFileRenamed,

    @SerialName("movieFileDeleted")
    MovieFileDeleted,

    @SerialName("movieFolderImported")
    MovieFolderImported,

    @SerialName("episodeFileRenamed")
    EpisodeFileRenamed,

    @SerialName("episodeFileDeleted")
    EpisodeFileDeleted,

    @SerialName("seriesFolderImported")
    SeriesFolderImported
}