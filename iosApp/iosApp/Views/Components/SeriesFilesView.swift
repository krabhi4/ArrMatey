//
//  SeriesFilesView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import SwiftUI
import Shared

struct SeriesFilesView: View {
    let series: ArrSeries
    let viewModel: SonarrViewModel
    
    @State private var episodeState: EpisodeUiState = EpisodeUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    private var allEpisodes: [Episode]? {
        if let state = episodeState as? EpisodeUiStateSuccess {
            state.items.sorted { $0.episodeNumber > $1.episodeNumber }
        } else { nil }
    }
    
    private func seasonEpisodes(seasonNumber: Int32) -> [Episode] {
        guard let allEpisodes else { return [] }
        return allEpisodes.filter { $0.seasonNumber == seasonNumber }
    }
    
    private var seasons: [Season] {
        series.seasons.sorted { $0.seasonNumber > $1.seasonNumber }
    }
    
    var body: some View {
        contentForState()
            .task {
                await setupViewModel()
                await viewModel.getEpsiodes(seriesId: series.id)
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        Section {
            ForEach(seasons, id: \.self) { season in
                SeasonCard(series: series, season: season, viewModel: viewModel, episodes: seasonEpisodes(seasonNumber: season.seasonNumber))
            }
        } header : {
            Text(String(localized: LocalizedStringResource("seasons")))
                .font(.system(size: 26, weight: .bold))
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        observationTask?.cancel()
        observationTask = Task {
            await observeEpisodeState()
        }
    }
    
    @MainActor
    private func observeEpisodeState() async {
        do {
            let flow = viewModel.getEpisodeState()
            for try await state in flow {
                self.episodeState = state
            }
        } catch {
            print("Error observing episodes: \(error)")
        }
    }
}
