//
//  ArrMediaViewModelWrapper.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-16.
//

import Shared
import SwiftUI
import Combine

@MainActor
class ArrMediaViewModelS: ObservableObject {
    private let viewModel: ArrMediaViewModel
    
    @Published private(set) var uiState: LibraryUiState = LibraryUiStateInitial()
    @Published private(set) var instanceData: InstanceData?
    @Published private(set) var addItemStatus: OperationStatus = OperationStatusIdle()
    
    private var cancellables = Set<AnyCancellable>()
    
    init(type: InstanceType) {
        self.viewModel = KoinBridge.shared.getArrMediaViewModel(type: type)
        startObserving()
    }
    
    private func startObserving() {
        viewModel.uiState.observeAsync { self.uiState = $0 }
        viewModel.instanceData.observeAsync { self.instanceData = $0 }
        viewModel.addItemStatus.observeAsync { self.addItemStatus = $0 }
    }
    
    func executeAutomaticSearch(_ seriesId: Int64) {
        viewModel.executeAutomaticSearch(seriesId: seriesId)
    }
    
    func updateViewType(_ type: ViewType) {
        viewModel.updateViewType(viewType: type)
    }
    
    func updateSortBy(_ sortBy: SortBy) {
        viewModel.updateSortBy(sortBy: sortBy)
    }
    
    func updateSortOrder(_ order: Shared.SortOrder) {
        viewModel.updateSortOrder(sortOrder: order)
    }
    
    func updateFilterBy(_ filterBy: FilterBy) {
        viewModel.updateFilterBy(filterBy: filterBy)
    }
    
    func refresh() {
        viewModel.refresh()
    }
}
