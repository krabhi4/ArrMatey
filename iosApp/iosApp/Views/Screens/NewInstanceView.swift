//
//  NewInstanceView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

struct NewInstanceView: View {
    
    @Environment(\.dismiss) var dismiss
    
    @ObservedObject private var viewModel = AddInstanceViewModelS()
    
    @State private var instanceType: InstanceType// = .sonarr
    
    init(initialType: InstanceType) {
        self.instanceType = initialType
    }
    
    private var uiState: AddInstanceUiState {
        viewModel.uiState
    }
    
    private var showInfoCard: Bool {
        uiState.infoCardMaps[instanceType]?.boolValue ?? false
    }
    
    private var createError: Bool {
        uiState.createResult is InsertResultError
    }
    
    private var createSuccess: Bool {
        uiState.createResult is InsertResultSuccess
    }
    
    var body: some View {
        content
            .toolbar { toolbarContent }
            .onChange(of: instanceType) { _, _ in
                viewModel.setInstanceLabel(instanceType.name)
            }
            .onChange(of: createSuccess) { _, newValue in
                if newValue {
                    dismiss()
                }
            }
    }
    
    @ViewBuilder
    private var content: some View {
        ArrConfigurationView(
            uiState: uiState,
            onApiEndpointChanged: { viewModel.setApiEndpoint($0) },
            onApiKeyChanged: { viewModel.setApiKey($0) },
            onInstanceLabelChanged: { viewModel.setInstanceLabel($0) },
            onIsSlowInstanceChanged: { viewModel.setIsSlowInstance($0) },
            onCustomTimeoutChanged: { viewModel.setCustomTimeout($0) },
            onTestConnection: { viewModel.testConnection() },
            onDismissInfoCard: { viewModel.dismissInfoCard($0) },
            showInfoCard: showInfoCard,
            showInstancePicker: true,
            instanceType: $instanceType,
            showError: $viewModel.showError
        )
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            Button(LocalizedStringResource("save")) {
                viewModel.createInstance(instanceType)
            }
            .disabled(!uiState.saveButtonEnabled)
        }
    }
}
