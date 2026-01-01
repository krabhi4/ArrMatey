//
//  AddSeriesForm.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-31.
//

import Shared
import SwiftUI

struct AddSeriesForm: View {
    @Binding var qualityProfiles: [QualityProfile]
    @Binding var rootFolders: [RootFolder]
    let onDismiss: () -> Void
    let series: ArrSeries
    
    @State private var monitorType: SeriesMonitorType = .all
    @State private var selectedQualityProfileId: Int32? = nil
    @State private var selectedSeriesType: SeriesType = .standard
    @State private var useSeasonFolders: Bool = true
    @State private var selectedRootFolderId: Int32? = nil
    
    @State private var addItemUiState: Any = DetailsUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel
    @EnvironmentObject private var navigation: NavigationManager
    
    private let selectableMonitorTypes: [SeriesMonitorType] = SeriesMonitorType.companion.allValues().filter {
        $0 != .unknown && $0 != .latestSeason && $0 != .skip
    }
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    private var successItem: ArrSeries? {
        (addItemUiState as? DetailsUiStateSuccess)?.item
    }
    
    private var isLoading: Bool {
        addItemUiState is DetailsUiStateLoading
    }
    
    private var selectedRootFolderPath: String? {
        rootFolders.first { $0.id == selectedRootFolderId }?.path
    }
    
    var body: some View {
        NavigationStack {
            content
                .toolbar {
                    toolbarButtons
                }
                .onChange(of: qualityProfiles, initial: true) {
                    if !qualityProfiles.isEmpty && selectedQualityProfileId == nil {
                        selectedQualityProfileId = qualityProfiles[0].id
                    }
                }
                .onChange(of: rootFolders, initial: true) {
                    if !rootFolders.isEmpty && selectedRootFolderId == nil {
                        selectedRootFolderId = rootFolders[0].id
                    }
                }
                .onChange(of: successItem) { _, newValue in
                    if let item = newValue, let id = item.id {
                        onDismiss()
                        navigation.replaceCurrent(with: .details(id.intValue), for: .sonarr)
                    }
                }
                .task {
                    await setupViewModel()
                }
                .onDisappear {
                    observationTask?.cancel()
                }
        }
    }
    
    @ViewBuilder
    private var content: some View {
        Form {
            Section {
                Picker("monitor", selection: $monitorType) {
                    ForEach(selectableMonitorTypes, id: \.self) { type in
                        Text(String(localized: LocalizedStringResource(stringLiteral: type.label()))).tag(type)
                    }
                }
                
                Toggle("season_folders", isOn: $useSeasonFolders)
            }
            
            Section {
                Picker("quality_profile", selection: $selectedQualityProfileId) {
                    ForEach(qualityProfiles, id: \.self) { qualityProfile in
                        if let name = qualityProfile.name {
                            Text(name).tag(qualityProfile.id)
                        }
                    }
                }
                
                Picker("series_type", selection: $selectedSeriesType) {
                    ForEach(SeriesType.companion.allEntries(), id: \.self) { seriesType in
                        Text(String(localized: LocalizedStringResource(stringLiteral: seriesType.label()))).tag(seriesType)
                    }
                }
                
                Picker("root_folder", selection: $selectedRootFolderId) {
                    ForEach(rootFolders, id: \.self) { rootFolder in
                        Text("\(rootFolder.path) (\(rootFolder.freeSpaceString))")
                            .tag(rootFolder.id)
                    }
                }
            }
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarButtons: some ToolbarContent {
        ToolbarItem(placement: .cancellationAction) {
            Button {
                onDismiss()
            } label: {
                Label("cancel", systemImage: "xmark")
            }
            .tint(.primary)
        }
        
        ToolbarItem(placement: .primaryAction) {
            Button {
                Task {
                    if let profileId = selectedQualityProfileId, let path = selectedRootFolderPath {
                        let newSeries = series.doCopyForCreation(monitor: monitorType, qualityProfileId: profileId, seriesType: selectedSeriesType, seasonFolder: useSeasonFolders, rootFolderPath: path)
                        await arrViewModel?.addItem(item: newSeries)
                    }
                }
            } label: {
                if (isLoading) {
                    ProgressView().tint(nil)
                } else {
                    Label("save", systemImage: "checkmark")
                }
            }
            .disabled(isLoading)
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        observationTask?.cancel()
        observationTask = Task {
            await observeAddItemState()
        }
    }
    
    @MainActor
    private func observeAddItemState() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.getAddItemUiState()
            for try await value in flow {
                self.addItemUiState = value
            }
        }
    }
}
