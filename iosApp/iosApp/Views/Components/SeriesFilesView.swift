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
    let episodes: [Episode]
    let searchIds: Set<Int64>
    let searchResult: Bool?
    let onToggleSeasonMonitor: (Int32) -> Void
    let onToggleEpisodeMonitor: (Episode) -> Void
    let onEpisodeAutomaticSearch: (Int64) -> Void
    let onSeasonAutomaticSearch: (Int32) -> Void
    
    @ObservedObject private var activityQueueViewModel = ActivityQueueViewModelS()
    
    private var queueItems: [QueueItem] {
        activityQueueViewModel.queueItems
    }
    
    private var seasonEpisodes: [Int32:[Episode]] {
        Dictionary(grouping: episodes, by: { $0.seasonNumber })
    }
    
    var body: some View {
        let sortedSeasons = series.seasons.sorted { $0.seasonNumber > $1.seasonNumber }
        Section {
            ForEach(sortedSeasons, id: \.self) { season in
                SeasonCard(
                    series: series,
                    season: season,
                    episodes: seasonEpisodes[season.seasonNumber] ?? [],
                    onToggleSeasonMonitor: onToggleSeasonMonitor,
                    onToggleEpisodeMonitor: onToggleEpisodeMonitor,
                    onEpisodeAutomaticSearch: onEpisodeAutomaticSearch,
                    onSeasonAutomaticSearch: onSeasonAutomaticSearch,
                    automaticSearchIds: searchIds
                )
            }
        } header : {
            Text(String(localized: LocalizedStringResource("seasons")))
                .font(.system(size: 26, weight: .bold))
        }
    }
}
