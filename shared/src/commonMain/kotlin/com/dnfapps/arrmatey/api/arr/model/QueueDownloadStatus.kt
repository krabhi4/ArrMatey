package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class QueueDownloadStatus {
    @SerialName("ok")
    Ok,

    @SerialName("warning")
    Warning,

    @SerialName("error")
    Error
}
