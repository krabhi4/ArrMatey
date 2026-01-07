package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.api.arr.viewmodel.RadarrRepository
import com.dnfapps.arrmatey.model.Instance
import kotlinx.coroutines.launch

class RadarrViewModel(instance: Instance): ArrViewModel(instance) {

    private val radarrRepo = repository as RadarrRepository

    val movieExtraFilesMap = radarrRepo.movieExtraFileMap

    fun getMovieExtraFile(id: Int) {
        viewModelScope.launch {
            radarrRepo.getMovieExtraFile(id)
        }
    }
}