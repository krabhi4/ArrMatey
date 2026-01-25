//
//  ArrSearchViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-19.
//

import Shared
import SwiftUI

@MainActor
class ArrSearchViewModelS: ObservableObject {
    private let viewModel: ArrSearchViewModel
    
    @Published private(set) var uiState: ArrLibrary = ArrLibraryInitial()
    @Published private(set) var sortBy: SortBy = .relevance
    @Published private(set) var sortOrder: Shared.SortOrder = .asc
    
    init(type: InstanceType) {
        self.viewModel = KoinBridge.shared.getArrSearchViewModel(type: type)
        startObserving()
    }
    
    private func startObserving() {
        viewModel.lookupUiState.observeAsync { self.uiState = $0 }
        
        Task {
            for try await state in viewModel.lookupUiState {
                self.uiState = state
            }
        }
        Task {
            for try await sortBy in viewModel.sortBy {
                self.sortBy = sortBy
            }
        }
        Task {
            for try await sortOrder in viewModel.sortOrder {
                self.sortOrder = sortOrder
            }
        }
    }
    
    func performLookup(_ query: String) {
        viewModel.performLookup(query: query)
    }
    
    func setSortBy(_ sortBy: SortBy) {
        viewModel.setSortBy(sortBy: sortBy)
    }
    
    func setSortOrder(_ sortOrder: Shared.SortOrder) {
        viewModel.setSortOrder(sortOrder: sortOrder)
    }
    
    func clearLookup() {
        viewModel.clearLookup()
    }
}
