package com.dnfapps.arrmatey.ui.viewmodel

import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.model.Instance

class RadarrViewModel(instance: Instance): ArrViewModel(instance) {
    override fun searchPayload(ids: List<Int>): CommandPayload {
        return CommandPayload.RadarrSearch(ids)
    }
}