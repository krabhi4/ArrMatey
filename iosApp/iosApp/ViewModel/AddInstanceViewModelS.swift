//
//  AddInstanceViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-19.
//

import Shared
import SwiftUI

@MainActor
class AddInstanceViewModelS: ObservableObject {
    private let viewModel: AddInstanceViewModel
    
    @Published private(set) var uiState: AddInstanceUiState
    @Published var showError: Bool = false
    
    init() {
        self.viewModel = KoinBridge.shared.getAddInstanceViewModel()
        self.uiState = AddInstanceUiState.companion.empty()
        startObserving()
    }
    
    private func startObserving() {
        viewModel.uiState.observeAsync {
            self.uiState = $0
            self.showError = $0.createResult is InsertResultSuccess
        }
    }
    
    func setApiEndpoint(_ endpoint: String) {
        viewModel.setApiEndpoint(endpoint: endpoint)
    }
    
    func setApiKey(_ key: String) {
        viewModel.setApiKey(value: key)
    }
    
    func setIsSlowInstance(_ isSlowInstance: Bool) {
        viewModel.setIsSlowInstance(value: isSlowInstance)
    }
    
    func setCustomTimeout(_ customTimeout: Int64?) {
        viewModel.setCustomTimeout(
            value: customTimeout.map(KotlinLong.init(value:))
        )
    }
    
    func setInstanceLabel(_ instanceLabel: String) {
        viewModel.setInstanceLabel(value: instanceLabel)
    }
    
    func reset() {
        viewModel.reset()
    }
    
    func dismissInfoCard(_ type: InstanceType) {
        viewModel.dismissInfoCard(instanceType: type)
    }
    
    func testConnection() {
        viewModel.testConnection()
    }
    
    func createInstance(_ type: InstanceType) {
        viewModel.createInstance(type: type)
    }
}
