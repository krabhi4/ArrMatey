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
    @State private var showEditSheet: Bool = false
    @State private var confirmDeleteSeason: Int32? = nil
    
    init(id: Int64, type: InstanceType) {
        self.id = id
        self.type = type
        self.viewModel = ArrMediaDetailsViewModelS(id: id, type: type)
    }
    
    var body: some View {
        contentForState()
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Image(systemName: viewModel.isMonitored ? "bookmark.fill" : "bookmark")
                        .imageScale(.medium)
                        .onTapGesture {
                            viewModel.toggleMonitor()
                        }
                }
                ToolbarItem(placement: .primaryAction) {
                    Menu {
                        Button("edit", systemImage: "pencil") {
                            showEditSheet = true
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
                DeleteMediaSheet(isLoading: viewModel.deleteInProgress, onConfirm: { addExclusion, deleteFiles in
                    viewModel.delete(addExclusion, deleteFiles)
                })
                .presentationDetents([.fraction(0.33)])
                .presentationBackground(.ultraThinMaterial)
            }
            .sheet(isPresented: $showEditSheet) {
                switch viewModel.item {
                case nil: EmptyView()
                case let movie as ArrMovie: EditMovieSheet(item: movie, qualityProfiles: viewModel.qualityProfiles, rootFolders: viewModel.rootFolders, tags: viewModel.tags, editInProgress: viewModel.editInProgress, onEditItem: { newMovie, moveFiles in
                    viewModel.editItem(newMovie, moveFiles: moveFiles)
                })
                        .presentationBackground(.ultraThinMaterial)
                case let series as ArrSeries: EditSeriesSheet(item: series, qualityProfiles: viewModel.qualityProfiles, rootFolders: viewModel.rootFolders, tags: viewModel.tags, editInProgress: viewModel.editInProgress, onEditItem: { newSeries, moveFiles in
                    viewModel.editItem(newSeries, moveFiles: moveFiles)
                })
                        .presentationBackground(.ultraThinMaterial)
                default: EmptyView()
                }
            }
            .onChange(of: viewModel.deleteSucceeded) { _, success in
                if success {
                    dismiss()
                }
            }
            .onChange(of: viewModel.editItemSucceeded) { _, success in
                if success {
                    showEditSheet = false
                    viewModel.refreshDetails()
                }
            }
            .alert(
                "Delete season \(confirmDeleteSeason ?? 0)?",
                isPresented: Binding(
                    get: { confirmDeleteSeason != nil },
                    set: { if !$0 { confirmDeleteSeason = nil } }
                ),
                presenting: confirmDeleteSeason
            ) { season in
                Button("delete", role: .destructive) {
                    viewModel.deleteSeasonFiles(season)
                    confirmDeleteSeason = nil
                }
                Button("cancel", role: .cancel) {
                    confirmDeleteSeason = nil
                }
            } message: { season in
                Text("Are you sure you want to remove all the files for season \(season)? This action cannot be undone.")
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        switch viewModel.uiState {
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
                        
                        MediaInfoArea(item: item, qualityProfiles: viewModel.qualityProfiles, tags: viewModel.tags)
                        
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
                searchIds: viewModel.automaticSearchIds,
                searchResult: viewModel.lastSearchResult,
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
                },
                onDeleteSeasonFiles: { seasonNumber in
                    confirmDeleteSeason = seasonNumber
                },
                seasonDeleteInProgress: false
            )
        } else if let movie = item as? ArrMovie {
            MovieFilesView(
                movie: movie,
                movieExtraFiles: extraFiles,
                searchIds: viewModel.automaticSearchIds,
                searchResult: viewModel.lastSearchResult,
                onAutomaticSearch: {
                    viewModel.performMovieAutomaticLookup(movieId: movie.id as! Int64)
                }
            )
        } else {
            EmptyView()
        }
    }
    
}
