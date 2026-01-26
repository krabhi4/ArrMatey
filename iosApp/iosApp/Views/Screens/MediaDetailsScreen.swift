//
//  MediaDetailsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

struct MediaDetailsScreen: View {
    private let id: Int64
    private let type: InstanceType
    
    @Environment(\.dismiss) private var dismiss

    @ObservedObject private var viewModel: ArrMediaDetailsViewModelS
    
    @State private var showConfirmSheet: Bool = false
    
    init(id: Int64, type: InstanceType) {
        self.id = id
        self.type = type
        self.viewModel = ArrMediaDetailsViewModelS(id: id, type: type)
    }
    
    private var uiState: MediaDetailsUiState {
        viewModel.uiState
    }
    
    private var automaticSearchIds: Set<Int64> {
        viewModel.automaticSearchIds
    }
    
    private var lastSearchResult: Bool? {
        viewModel.lastSearchResult
    }
    
    private var isMonitored: Bool {
        viewModel.isMonitored
    }
    
    private var qualityProfiles: [QualityProfile] {
        viewModel.qualityProfiles
    }
    
    private var tags: [Tag] {
        viewModel.tags
    }
    
    private var deleteInProgress: Bool {
        viewModel.deleteStatus is OperationStatusInProgress
    }
    
    var body: some View {
        contentForState()
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Image(systemName: isMonitored ? "bookmark.fill" : "bookmark")
                        .imageScale(.medium)
                        .onTapGesture {
                            viewModel.toggleMonitor()
                        }
                }
                ToolbarItem(placement: .primaryAction) {
                    Menu {
                        Button("edit", systemImage: "pencil") {
                            // todo
                        }
                        Button("delete", systemImage: "trash") {
                            showConfirmSheet = true
                        }
                        .tint(.red)
                    } label: {
                        Image(systemName: "ellipsis")
                            .imageScale(.medium)
                    }
                }
            }
            .task {
                viewModel.refreshDetails()
            }
            .sheet(isPresented: $showConfirmSheet) {
                DeleteMediaSheet(isLoading: deleteInProgress, onConfirm: { addExclusion, deleteFiles in
                    viewModel.delete(addExclusion, deleteFiles)
                })
                .presentationDetents([.fraction(0.33)])
                .presentationBackground(.ultraThinMaterial)
            }
            .onChange(of: viewModel.deleteSucceeded) { _, success in
                if success {
                    dismiss()
                }
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        switch uiState {
        case is MediaDetailsUiStateInitial:
            ZStack {
                Text("initial state")
            }
        case is MediaDetailsUiStateLoading:
            ZStack {
                ProgressView()
                    .progressViewStyle(.circular)
            }
        case let state as MediaDetailsUiStateSuccess:
            let item = state.item
            let episodes = state.episodes
            let extraFiles = state.extraFiles
            
            ScrollView {
                VStack(alignment: .leading, spacing: 12){
                    MediaDetailsHeader(item: item)
                    
                    VStack(alignment: .leading, spacing: 12) {
                        if let airingString = makeAiringString(for: item) {
                            Text(airingString)
                                .font(.system(size: 20, weight: .medium))
                                .foregroundColor(.accentColor)
                        }
                        
                        ItemDescriptionCard(overview: item.overview)
                        
                        filesArea(for: item, extraFiles, episodes)
                        
                        MediaInfoArea(item: item, qualityProfiles: qualityProfiles, tags: tags)
                        
                        Spacer()
                            .frame(height: 12)
                    }
                    .padding(.horizontal, 24)
                }
                .frame(alignment: .top)
            }
            .ignoresSafeArea(edges: .top)
        case _ as MediaDetailsUiStateError:
            VStack{}
        default:
            VStack {
                Text("detault")
            }
        }
    }
    
    private func makeAiringString(for item: ArrMedia) -> String? {
        switch item {
        case let series as ArrSeries:
            if series.status == .continuing {
                if let airing = series.nextAiring?.format(pattern: "HH:mm MMMM d, yyyy") {
                    return "\(String(localized: LocalizedStringResource("airing_next"))) \(airing)"
                } else {
                    return String(localized: LocalizedStringResource("continuing_unknown"))
                }
            } else { return nil }
        case let movie as ArrMovie:
            if let inCinemas = movie.inCinemas?.format(pattern: "HH:mm MMMM d, yyyy"), movie.digitalRelease == nil, movie.physicalRelease == nil {
                return "\(String(localized: LocalizedStringResource("in_cinemas"))) \(inCinemas)"
            } else {
                return nil
            }
        default: return nil
        }
    }
    
    @ViewBuilder
    private func filesArea(
        for item: ArrMedia,
        _ extraFiles: [ExtraFile],
        _ episodes: [Episode]
    ) -> some View {
        if let series = item as? ArrSeries {
            SeriesFilesView(
                series: series,
                episodes: episodes,
                searchIds: automaticSearchIds,
                searchResult: lastSearchResult,
                onToggleSeasonMonitor: { sn in
                    viewModel.toggleSeasonMonitor(seasonNumber: sn)
                },
                onToggleEpisodeMonitor: { ep in
                    viewModel.toggleEpisodeMonitor(episode: ep)
                },
                onEpisodeAutomaticSearch: { id in
                    viewModel.performEpisodeAutomaticLookup(episodeId: id)
                },
                onSeasonAutomaticSearch: { sn in
                    viewModel.performSeasonAutomaticLookup(seasonNumber: sn)
                }
            )
        } else if let movie = item as? ArrMovie {
            MovieFilesView(
                movie: movie,
                movieExtraFiles: extraFiles,
                searchIds: automaticSearchIds,
                searchResult: lastSearchResult,
                onAutomaticSearch: {
                    viewModel.performMovieAutomaticLookup(movieId: movie.id as! Int64)
                }
            )
        } else {
            EmptyView()
        }
    }
    
}
