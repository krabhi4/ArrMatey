//
//  AddMovieForm.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-30.
//

import Shared
import SwiftUI

struct AddMovieForm: View {
    @Binding var qualityProfiles: [QualityProfile]
    @Binding var rootFolders: [RootFolder]
    let onDismiss: () -> Void
    let movie: ArrMovie
    
    @State private var isMonitored: Bool = true
    @State private var selectedMinimumAvailability: MovieStatus = .announced
    @State private var selectedQualityProfileId: Int32? = nil
    @State private var selectedRootFolderId: Int32? = nil
    
    @State private var addItemUiState: Any = DetailsUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel
    @EnvironmentObject private var navigation: NavigationManager
    
    private let selectableStatuses: [MovieStatus] = [
        .announced,
        .inCinemas,
        .released,
    ]
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    private var successItem: ArrMovie? {
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
                .task {
                    await setupViewModel()
                }
                .onDisappear {
                    observationTask?.cancel()
                }
                .onChange(of: successItem) { _, newValue in
                    if let item = newValue, let id = item.id {
                        onDismiss()
                        navigation.replaceCurrent(with: .details(id.intValue), for: .radarr)
                    }
                }
        }
    }
    
    @ViewBuilder
    private var content: some View {
        Form {
            Section {
                Toggle("monitored", isOn: $isMonitored)
                
                if selectedQualityProfileId != nil {
                    Picker("quality_profile", selection: $selectedQualityProfileId) {
                        ForEach(qualityProfiles, id: \.self) { qualityProfile in
                            if let name = qualityProfile.name {
                                Text(name)
                                    .tag(qualityProfile.id)
                            }
                        }
                    }
                }
                
                Picker("minimum_availability", selection: $selectedMinimumAvailability) {
                    ForEach(selectableStatuses, id: \.self) { status in
                        Text(String(localized: LocalizedStringResource(stringLiteral: status.label()))).tag(status)
                    }
                }
                
                if selectedRootFolderId != nil {
                    Picker("root_folder", selection: $selectedRootFolderId) {
                        ForEach(rootFolders, id: \.self) { rootFolder in
                            Text("\(rootFolder.path) (\(rootFolder.freeSpaceString))")
                                .tag(rootFolder.id)
                        }
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
                        let newMovie = movie.doCopyForCreation(monitored: isMonitored, minimumAvailability: selectedMinimumAvailability, qualityProfileId: profileId, rootFolderPath: path)
                        await arrViewModel?.addItem(item: newMovie)
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
