//
//  EpisodeDetailsViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-19.
//

import Shared
import SwiftUI

@MainActor
class EpisodeDetailsViewModelS: ObservableObject {
    private let viewModel: EpisodeDetailsViewModel
    
    @Published private(set) var episode: Episode
    @Published private(set) var history: NetworkResult? = nil
    @Published private(set) var monitorStatus: OperationStatus = OperationStatusIdle()
    
    init(seriesId: Int64, episode: Episode) {
        self.episode = episode
        self.viewModel = KoinBridge.shared.getEpisodeDetailsViewModel(seriesId: seriesId, episode: episode)
        startObserving()
    }
    
    private func startObserving() {
        viewModel.episode.observeAsync { self.episode = $0 }
        viewModel.history.observeAsync { self.history = $0 }
        viewModel.monitorStatus.observeAsync { self.monitorStatus = $0 }
    }
    
    func toggleMonitor() {
        viewModel.toggleMonitor()
    }
    
    func executeAutomaticSearch() {
        viewModel.executeAutomaticSearch()
    }
    
    func refreshHistory() {
        viewModel.refreshHistory()
    }
    
    func resetMonitorStatus() {
        viewModel.resetMonitorStatus()
    }
}
