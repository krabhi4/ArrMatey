//
//  InteractiveSearchViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-19.
//

import Shared
import SwiftUI

@MainActor
class InteractiveSearchViewModelS: ObservableObject {
    private let viewModel: InteractiveSearchViewModel
    
    @Published private(set) var releaseUiState: LibraryUiState = LibraryUiStateInitial()
    @Published private(set) var downloadReleaseState: DownloadState = DownloadStateInitial()
    @Published private(set) var filterUiState: InteractiveSearchUiState
    @Published private(set) var downloadStatus: Bool? = nil
    @Published var searchQuery: String = "" {
        didSet {
            updateSearchQuery(searchQuery)
        }
    }
    
    init(type: InstanceType, defaultFilter: ReleaseFilterBy) {
        self.viewModel = KoinBridge.shared.getInteractiveSearchViewModel(type: type, defaultFilter: defaultFilter)
        self.filterUiState = InteractiveSearchUiState.companion.empty(filterBy: defaultFilter)
        startObserving()
    }
    
    private func startObserving() {
        viewModel.releaseUiState.observeAsync { self.releaseUiState = $0 }
        viewModel.downloadReleaseState.observeAsync { self.downloadReleaseState = $0 }
        viewModel.filterUiState.observeAsync { self.filterUiState = $0 }
        viewModel.downloadStatus.observeAsync { self.downloadStatus = $0?.boolValue }
        viewModel.searchQuery.observeAsync { self.searchQuery = $0 }
    }
    
    func getRelease(_ params: ReleaseParams) {
        viewModel.getRelease(params: params)
    }
    
    func downloadRelease(_ release: ArrRelease, _ force: Bool = false) {
        viewModel.downloadRelease(release: release, force: force)
    }
    
    func resetDownloadState() {
        viewModel.resetDownloadState()
    }
    
    func updateSearchQuery(_ query: String) {
        viewModel.updateSearchQuery(query: query)
    }
    
    func setSortOrder(_ sortOrder: Shared.SortOrder) {
        viewModel.setSortOrder(sortOrder: sortOrder)
    }
    
    func setSortBy(_ sortBy: ReleaseSortBy) {
        viewModel.setSortBy(sortBy: sortBy)
    }
    
    func setFilterby(_ filterBy: ReleaseFilterBy) {
        viewModel.setFilterBy(filterBy: filterBy)
    }
}
