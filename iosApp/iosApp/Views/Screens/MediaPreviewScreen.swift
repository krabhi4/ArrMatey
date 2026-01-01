//
//  MediaPreviewScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-29.
//

import SwiftUI
import Shared

struct MediaPreviewScreen: View {
    private let media: AnyArrMedia

    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel

    @State private var sheetPresented: Bool = false
    
    @State private var qualityProfiles: [QualityProfile] = []
    @State private var rootFolders: [RootFolder] = []
    @State private var tags: [Tag] = []
    
    @State private var observeQualityProfilesTask: Task<Void, Never>? = nil
    @State private var observeRootFoldersTask: Task<Void, Never>? = nil
    @State private var observeTagsTask: Task<Void, Never>? = nil


    private var instance: Instance? {
        arrTabViewModel.currentInstance
    }
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    init(json: String) {
        media = AnyArrMediaCompanion().fromJson(value: json)
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                MediaDetailsHeader(item: media)

                VStack(alignment: .leading, spacing: 12) {
                    if let airingString = makeAiringString(for: media) {
                        Text(airingString)
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(.accentColor)
                    }

                    ItemDescriptionCard(overview: media.overview)
                }
                .padding(.horizontal, 24)
            }
            .frame(alignment: .top)
        }
        .ignoresSafeArea(edges: .top)
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button("add", systemImage: "plus") {
                    sheetPresented = true
                }
            }
        }
        .sheet(isPresented: $sheetPresented) {
            switch media {
            case let series as ArrSeries:
                AddSeriesForm(qualityProfiles: $qualityProfiles, rootFolders: $rootFolders, onDismiss: { sheetPresented = false }, series: series)
                    .presentationDetents([.medium])
                    .presentationBackground(.ultraThinMaterial)
            case let movie as ArrMovie:
                AddMovieForm(qualityProfiles: $qualityProfiles, rootFolders: $rootFolders, onDismiss: { sheetPresented = false }, movie: movie)
                    .presentationDetents([.medium])
                    .presentationBackground(.ultraThinMaterial)
            default: EmptyView()
            }
        }
        .task {
            await setupViewModel()
        }
        .onDisappear {
            observeQualityProfilesTask?.cancel()
            observeRootFoldersTask?.cancel()
            observeTagsTask?.cancel()
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
    private func seriesForm() -> some View {
        
    }
    
    @MainActor
    private func setupViewModel() async {
        observeQualityProfilesTask?.cancel()
        observeQualityProfilesTask = Task {
            await observeQualityProfiles()
        }
        
        observeRootFoldersTask?.cancel()
        observeRootFoldersTask = Task {
            await observeRootFolders()
        }
        
        observeTagsTask?.cancel()
        observeTagsTask = Task {
            await observeTags()
        }
    }
    
    @MainActor
    private func observeQualityProfiles() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let qpFlow = viewModel.qualityProfiles()
            for try await qp in qpFlow {
                self.qualityProfiles = qp
            }
        }
    }
    
    @MainActor
    private func observeRootFolders() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.rootFolders()
            for try await rootFolders in flow {
                self.rootFolders = rootFolders
            }
        }
    }
    
    @MainActor
    private func observeTags() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.tags()
            for try await tags in flow {
                self.tags = tags
            }
        }
    }
}
