package com.dnfapps.arrmatey.instances.state

import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.instances.model.InstanceHeader
import com.dnfapps.arrmatey.instances.model.InstanceType

data class AddInstanceUiState(
    val apiEndpoint: String = "",
    val apiKey: String = "",
    val instanceLabel: String = "",
    val isSlowInstance: Boolean = false,
    val customTimeout: Long? = null,
    val endpointError: Boolean = false,
    val testing: Boolean = false,
    val testResult: Boolean? = null,
    val saveButtonEnabled: Boolean = false,
    val createResult: InsertResult? = null,
    val editResult: InsertResult? = null,
    val infoCardMaps: Map<InstanceType, Boolean> = emptyMap(),
    val headers: List<InstanceHeader> = emptyList()
) {
    constructor(): this(
        "", "", "", false,
        null, false, false, null,
        false, null, null,
        emptyMap(), emptyList())
}