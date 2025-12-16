//
//  SonarrViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import Shared

class SonarrViewModel: ArrViewModel {
    var repository: any IArrRepository
    
    let instance: Instance

    private var sonarrRepository: SonarrRepository {
        repository as! SonarrRepository
    }
    
    init(instance: Instance) {
        self.instance = instance
        self.repository = SonarrRepository(instance: instance)
    }
    
    func getEpisodeState() -> SkieSwiftStateFlow<EpisodeUiState> {
        return sonarrRepository.episodeState
    }
    
    func getEpsiodes(seriesId: Int32, seasonNumber: Int32? = nil) async {
        do {
            try await sonarrRepository.getEpisodes(seriesId: seriesId, seasonNumber: seasonNumber as? KotlinInt)
        } catch {
            return
        }
    }
    
    func toggleSeasonMonitor(series: ArrSeries, seasonNumber: Int32) async {
        do {
            try await sonarrRepository.toggleSeasonMonitorState(series: series, seasonNumber: seasonNumber)
        } catch {
            return
        }
    }
    
    func toggleEpisodeMonitor(episodeId: Int64) async {
        do {
            try await sonarrRepository.toggleEpisodeMonitorState(episodeId: episodeId)
        } catch {
            return
        }
    }
}
