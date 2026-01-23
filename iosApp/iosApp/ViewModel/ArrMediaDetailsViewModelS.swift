//
//  ArrMediaDetailsViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-20.
//

import Shared
import SwiftUI

@MainActor
class ArrMediaDetailsViewModelS: ObservableObject {
    private let viewModel: ArrMediaDetailsViewModel
    
    @Published private(set) var uiState: MediaDetailsUiState = MediaDetailsUiStateInitial()
    @Published private(set) var history: [HistoryItem] = []
    @Published private(set) var monitorStatus: OperationStatus = OperationStatusIdle()
    @Published private(set) var isMonitored: Bool = false
    @Published private(set) var automaticSearchIds: Set<Int64> = Set()
    @Published private(set) var lastSearchResult: Bool? = nil
    @Published private(set) var qualityProfiles: [QualityProfile] = []
    @Published private(set) var tags: [Tag] = []
    
    init(id: Int64, type: InstanceType) {
        self.viewModel = KoinBridge.shared.getArrMediaDetailsViewModel(id: id, type: type)
        startObserving()
    }
    
    private func startObserving() {
        viewModel.uiState.observeAsync { self.uiState = $0 }
        viewModel.history.observeAsync { self.history = $0 }
        viewModel.monitorStatus.observeAsync { self.monitorStatus = $0 }
        viewModel.isMonitored.observeAsync { self.isMonitored = $0.boolValue }
        viewModel.automaticSearchIds.observeAsync { self.automaticSearchIds = Set($0.map { $0.int64Value }) }
        viewModel.lastSearchResult.observeAsync { self.lastSearchResult = $0?.boolValue }
        viewModel.qualityProfiles.observeAsync { self.qualityProfiles = $0 }
        viewModel.tags.observeAsync { self.tags = $0 }
    }
    
    func refreshDetails() {
        viewModel.refreshDetails()
    }
    
    func toggleMonitor() {
        viewModel.toggleMonitored()
    }
    
    func toggleSeasonMonitor(seasonNumber: Int32) {
        viewModel.toggleSeasonMonitored(seasonNumber: seasonNumber)
    }
    
    func toggleEpisodeMonitor(episode: Episode) {
        viewModel.toggleEpisodeMonitored(episode: episode)
    }
    
    func performEpisodeAutomaticLookup(episodeId: Int64) {
        viewModel.performEpisodeAutomaticLookup(episodeId: episodeId)
    }
    
    func performSeasonAutomaticLookup(seasonNumber: Int32) {
        viewModel.performSeasonAutomaticLookup(seasonNumber: seasonNumber)
    }
    
    func performMovieAutomaticLookup(movieId: Int64) {
        viewModel.performMovieAutomaticLookup(movieId: movieId)
    }
}
