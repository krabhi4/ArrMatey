//
//  EpisodeDetailsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import SwiftUI
import Shared

struct EpisodeDetailsScreen: View {
    private let series: ArrSeries
    private let episode: Episode
    
    @ObservedObject private var viewModel: EpisodeDetailsViewModelS
    
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject private var navigation: NavigationManager
    
    init(seriesJson: String, episodeJson: String) {
        self.series = ArrMediaCompanion().fromJson(value: seriesJson) as! ArrSeries
        self.episode = Episode.companion.fromJson(json: episodeJson)
        
        self.viewModel = EpisodeDetailsViewModelS(seriesId: series.id as! Int64, episode: episode)
    }
    
    var body: some View {
        contentForState()
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Image(systemName: viewModel.episode.monitored ? "bookmark.fill" : "bookmark")
                        .imageScale(.medium)
                        .onTapGesture {
                            viewModel.toggleMonitor()
                        }
                }
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                EpisodeDetailsHeader(series: series, episode: episode)
                
                VStack(alignment: .leading, spacing: 12) {
                    ItemDescriptionCard(overview: episode.overview)
                    
                    ReleaseDownloadButtons(
                        onInteractiveClicked: {
                            navigation.go(to: .seriesReleases(episodeId: episode.id), of: .sonarr)
                        },
                        automaticSearchEnabled: viewModel.episode.monitored,
                        onAutomaticClicked: {
                            viewModel.executeAutomaticSearch()
                        })
                    
                    Text(String(localized: LocalizedStringResource("files")))
                        .font(.system(size: 20, weight: .bold))
                    
                    if let file = episode.episodeFile {
                        MediaFileCard(file: file)
                    }
                    
                    switch viewModel.history {
                    case is HistoryStateLoading:
                        ProgressView()
                            .progressViewStyle(.circular)
                    case let success as HistoryStateSuccess:
                        if success.items.isEmpty {
                            Text("no_history")
                                .font(.system(size: 22, weight: .medium))
                        } else {
                            ForEach(success.items, id: \.id) { historyItem in
                                HistoryItemView(item: historyItem)
                            }
                        }
                    default:
                        EmptyView()
                    }
                    
                    Spacer()
                        .frame(height: 12)
                }
                .padding(.horizontal, 24)
            }
            .frame(alignment: .top)
        }
        .ignoresSafeArea(edges: .top)
    }
}
