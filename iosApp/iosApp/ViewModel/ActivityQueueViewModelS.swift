//
//  ActivityQueueViewModelWrapper.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-16.
//

import Shared
import SwiftUI

@MainActor
class ActivityQueueViewModelS: ObservableObject {
    private let viewModel: ActivityQueueViewModel
    
    @Published private(set) var queueItems: [QueueItem] = []
    @Published private(set) var tasksWithIssues: Int = 0
    @Published private(set) var isPolling: Bool = false
    @Published private(set) var instance: [Instance] = []
    
    init() {
        self.viewModel = KoinBridge.shared.getActivityQueueViewModel()
        startObserving()
    }
    
    private func startObserving() {
        viewModel.queueItems.observeAsync { self.queueItems = $0 }
        viewModel.tasksWithIssues.observeAsync { self.tasksWithIssues = $0.intValue }
        viewModel.isPolling.observeAsync { self.isPolling = $0.boolValue }
        viewModel.instances.observeAsync { self.instance = $0 }
    }
    
    func startPolling() {
        viewModel.startPolling()
    }
    
    func stopPolling() {
        viewModel.stopPolling()
    }
    
    func setInstanceId(_ id: Int64?) {
        viewModel.setInstanceId(id: id?.asKotlinLong)
    }
    
    func setSortBy(_ sortBy: QueueSortBy) {
        viewModel.setSortBy(sortBy: sortBy)
    }
    
    func setSortOrder(_ order: Shared.SortOrder) {
        viewModel.setSortOrder(order: order)
    }
    
}
