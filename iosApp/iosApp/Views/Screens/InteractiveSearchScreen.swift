//
//  InteractiveSearchScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import Shared
import SwiftUI

struct InteractiveSearchScreen: View {
    
    private let canFilter: Bool
    private let releaseParams: ReleaseParams
    
    @ObservedObject private var viewModel: InteractiveSearchViewModelS
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var searchPresented: Bool = false
    @State private var confirmRelease: ArrRelease? = nil
    
    init(type: InstanceType, canFilter: Bool, releaseParams: ReleaseParams, defaultFilter: ReleaseFilterBy = .any) {
        self.canFilter = canFilter
        self.releaseParams = releaseParams
        self.viewModel = InteractiveSearchViewModelS(type: type, defaultFilter: defaultFilter)
    }
    
    var body: some View {
        ZStack {
            contentForState()
        }
        .searchable(text: Binding(get: { viewModel.searchQuery }, set: { viewModel.updateSearchQuery($0) }), isPresented: $searchPresented, prompt: "search")
        .onAppear {
            viewModel.getRelease(releaseParams)
        }
        .alert("grab_release", isPresented: Binding(get: { confirmRelease != nil }, set: { if !$0 { confirmRelease = nil }})) {
            Button("grab") {
                if let release = confirmRelease {
                    viewModel.downloadRelease(release, true)
                }
                confirmRelease = nil
            }
            Button("cancel", role: .cancel) { confirmRelease = nil }
        } message: {
            if let release = confirmRelease {
                Text("Are you sure you want to grab \(release.title)")
            }
        }
        .toolbar {
            toolbarContent
        }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        switch viewModel.releaseUiState {
        case is ReleaseLibraryLoading:
            ProgressView()
                .progressViewStyle(.circular)
        case let success as ReleaseLibrarySuccess:
            ScrollView {
                LazyVStack(spacing: 18) {
                    ForEach(success.items, id: \.guid) { item in
                        let isLoading = (viewModel.downloadReleaseState as? DownloadStateLoading)?.guid == item.guid
                        
                        ReleaseItemView(item: item, animate: isLoading, onItemClick: { release in
                            if release.downloadAllowed {
                                viewModel.downloadRelease(release, false)
                            } else {
                                confirmRelease = release
                            }
                        })
                    }
                }
                .padding(.horizontal, 18)
            }
        case let error as ReleaseLibraryError:
            Text(error.message)
                .foregroundColor(.red)
        default:
            EmptyView()
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            ReleaseSortByPickerMenu(
                sortBy: Binding(
                    get: { viewModel.filterUiState.sortBy },
                    set: { viewModel.setSortBy($0) }
                ),
                sortOrder: Binding(
                    get: { viewModel.filterUiState.sortOrder },
                    set: { viewModel.setSortOrder($0) }
                )
            )
            .menuIndicator(.hidden)
        }
        
        ToolbarItem(placement: .primaryAction) {
            ReleaseFilterByPickerMenu(filterBy: Binding(
                get: { viewModel.filterUiState.filterBy },
                set: { viewModel.setFilterby($0) }
            ))
            .menuIndicator(.hidden)
        }
    }
}
