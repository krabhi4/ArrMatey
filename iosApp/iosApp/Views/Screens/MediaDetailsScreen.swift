//
//  MediaDetailsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

struct MediaDetailsScreen: View {
    let id: Int
    let type: InstanceType
    
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel
    
    @State private var detailUiState: Any = DetailsUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    private var instance: Instance? {
        arrTabViewModel.currentInstance
    }
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    private var isMonitored: Bool {
        if let state = detailUiState as? DetailsUiStateSuccess<AnyArrMedia> {
            return state.item?.monitored == true
        }
        return false
    }
    
    var body: some View {
        contentForState()
            .task {
                await setupViewModel()
                if let vm = arrViewModel {
                    await vm.getDetails(id: Int32(id))
                }
            }
            .onDisappear {
                observationTask?.cancel()
            }
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Image(systemName: isMonitored ? "bookmark.fill" : "bookmark")
                        .imageScale(.medium)
                        .onTapGesture {
                            Task {
                                await arrViewModel?.setMonitorStatus(id: Int32(id), isMonitored: !isMonitored)
                            }
                        }
                }
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        switch detailUiState {
        case is DetailsUiStateInitial:
            ZStack {
                Text("initial state")
            }
        case is DetailsUiStateLoading:
            ZStack {
                ProgressView()
                    .progressViewStyle(.circular)
            }
        case let state as DetailsUiStateSuccess<AnyArrMedia>:
            if let item = state.item {
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
                            
                            filesArea(for: item)
                            
                            MediaInfoArea(item: item)
                            
                            Spacer()
                                .frame(height: 12)
                        }
                        .padding(.horizontal, 24)
                    }
                    .frame(alignment: .top)
                }
                .ignoresSafeArea(edges: .top)
            }
        case let error as DetailsUiStateError<AnyArrMedia>:
            VStack{}
        default:
            VStack {
                Text("detault")
            }
        }
    }
    
    private func makeAiringString(for item: AnyArrMedia) -> String? {
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
    private func filesArea(for item: AnyArrMedia) -> some View {
        if let series = item as? ArrSeries, let vm = arrViewModel as? SonarrViewModel {
            SeriesFilesView(series: series, viewModel: vm)
        } else if let movie = item as? ArrMovie {
            MovieFilesView(movie: movie)
        } else {
            EmptyView()
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        observationTask?.cancel()
        observationTask = Task {
            await observeUiState()
        }
    }
    
    @MainActor
    private func observeUiState() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.getDetailsUiState()
            for try await state in flow {
                self.detailUiState = state
            }
        }
    }
}
