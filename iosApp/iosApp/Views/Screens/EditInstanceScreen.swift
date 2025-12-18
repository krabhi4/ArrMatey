//
//  EditInstanceScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-17.
//

import Shared
import SwiftUI

struct EditInstanceScreen: View {
    let id: Int64
    
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject private var instanceViewModel: InstanceViewModel
    
    @StateObject private var viewModel = AddInstanceViewModel()
    @State private var showDeleteConfirmation: Bool = false
    @State private var saveClicked: Bool = false
    
    // MARK: - Derived values
    
    var instance: Instance? {
        instanceViewModel.instances.first { $0.id == id }
    }
    
    var urlPlaceholder: String {
        if let instance = instance {
            String(localized: LocalizedStringResource("host_placeholder")) + String(instance.type.defaultPort)
        } else { "" }
    }
    
    var hasError: Bool {
        viewModel.editResult is InsertResultError || viewModel.editResult is InsertResultConflict
    }
    
    var hasLabelConflict: Bool {
        guard let result = viewModel.editResult as? InsertResultConflict else { return false }
        return result.fields.contains(.instanceLabel)
    }
    
    var hasUrlConflict: Bool {
        guard let result = viewModel.createResult as? InsertResultConflict else { return false }
        return result.fields.contains(.instanceUrl)
    }
    
    var wasUpdatedSuccessfully: Bool {
        viewModel.editResult is InsertResultSuccess
    }
    
    // MARK: - Body
    
    var body: some View {
        content
            .toolbar { toolbarContent }
            .task { initializeIfNeeded() }
            .onChange(of: viewModel.result) { _, newValue in
                handleTestResultChange(newValue)
            }
            .onChange(of: viewModel.wasCreatedSuccessfully) { _, newValue in
                if newValue {
                    dismiss()
                }
            }
            .alert(String(localized: LocalizedStringResource("error")), isPresented: $viewModel.hasCreationError) {
                Button("ok") { viewModel.hasCreationError = false }
            } message: {
                errorMessageView
            }
            .alert(String(localized: LocalizedStringResource("confirm")), isPresented: $showDeleteConfirmation) {
                if let instance = instance {
                    Button("yes", role: .destructive) {
                        instanceViewModel.delete(instance)
                        dismiss()
                    }
                }
                Button("no", role: .cancel) {
                    showDeleteConfirmation = false
                }
            } message: {
                Text(String(localized: LocalizedStringResource("confirm_delete_instance \(instance?.label ?? "")")))
            }
    }
        
    // MARK: - Main content
    
    private var content: some View {
        Form {
            instanceSection
            testSection
            slowInstanceSection
        }
    }
    
    // MARK: - Sections
    
    @ViewBuilder
    private var instanceSection: some View {
        Section {
            if let instance = instance {
                HStack(spacing: 24) {
                    Text(LocalizedStringResource("label")).layoutPriority(2)
                    TextField(text: $viewModel.instanceLabel, prompt: Text(instance.type.name)) { EmptyView() }
                        .multilineTextAlignment(.trailing)
                        .onChange(of: viewModel.instanceLabel) { _, newValue in
                            viewModel.setInstanceLabel(newValue)
                        }
                }
                
                HStack(spacing: 24) {
                    Text(LocalizedStringResource("host")).layoutPriority(2)
                    TextField(text: $viewModel.apiEndpoint, prompt: Text(urlPlaceholder)) { EmptyView() }
                        .multilineTextAlignment(.trailing)
                        .textInputAutocapitalization(.never)
                        .onChange(of: viewModel.apiEndpoint) { _, newValue in
                            viewModel.setApiEndpoint(newValue)
                        }
                }
                
                HStack(spacing: 24) {
                    Text(LocalizedStringResource("api_key"))
                    TextField(text: $viewModel.apiKey, prompt: Text(LocalizedStringResource("api_key_placeholder"))) { EmptyView() }
                        .multilineTextAlignment(.trailing)
                        .textInputAutocapitalization(.never)
                        .onChange(of: viewModel.apiKey) { _, newValue in
                            viewModel.setApiKey(newValue)
                        }
                }
                
            }
        } footer: {
            if let instance = instance {
                Text(LocalizedStringKey("host_description \(String(instance.type.name))"))
            } else { EmptyView() }
        }
    }
    
    @ViewBuilder
    private var testSection: some View {
        HStack {
            Button(action : {
                viewModel.testConnection()
            }) {
                if viewModel.testing {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .frame(width: 20, height: 20)
                        .scaleEffect(0.8)
                } else {
                    Text(LocalizedStringResource("test"))
                }
            }
            .disabled(viewModel.testing || viewModel.apiKey.isEmpty || viewModel.apiEndpoint.isEmpty)
            
            if let result = viewModel.result {
                Spacer()
                Text(result ? "✅ \(LocalizedStringResource("success"))" : "❌ \(LocalizedStringResource("failure"))")
            }
        }
    }
    
    @ViewBuilder
    private var slowInstanceSection: some View {
        Section {
            Toggle(LocalizedStringResource("slow_instance"), isOn: $viewModel.isSlowInstance)
                .onChange(of: viewModel.isSlowInstance) { _, newValue in
                    viewModel.setIsSlowInstance(newValue)
                }
            HStack(spacing: 24) {
                Text(LocalizedStringResource("custom_timeout_seconds"))
                    .foregroundStyle(viewModel.isSlowInstance ? Color.primary.opacity(1.0) : Color.primary.opacity(0.3))
                TextField(text: viewModel.customTimeoutTextBinding, prompt: Text("300")) { EmptyView() }
                    .multilineTextAlignment(.trailing)
                    .keyboardType(.numberPad)
                    .onChange(of: viewModel.customTimeout) { _, newValue in
                        viewModel.setCustomTimeout(newValue)
                    }
                    .disabled(!viewModel.isSlowInstance)
            }
        }
    }
    
    // MARK: - Toolbar
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        if instance != nil {
            ToolbarItem(placement: .primaryAction) {
                Image(systemName: "trash")
                    .imageScale(.medium)
                    .foregroundColor(.red)
                    .onTapGesture {
                        showDeleteConfirmation = true
                    }
            }
            ToolbarItem(placement: .primaryAction) {
                Button(LocalizedStringResource("save")) {
                    Task {
                        saveClicked = true
                        viewModel.testConnection()
                    }
                }
            }
        }
    }
    
    // MARK: - Error alert view
    
    @ViewBuilder
    private var errorMessageView: some View {
        if let error = viewModel.editResult as? InsertResultError {
            Text(error.message)
        } else {
            if hasLabelConflict {
                Text(String(localized: LocalizedStringResource("instance_label_exists")))
            }
            if hasUrlConflict {
                Text(String(localized: LocalizedStringResource("instance_url_exists")))
            }
        }
    }
    
    // MARK: - Side effects
    
    private func initializeIfNeeded() {
        guard let instance = instance else { return }
        viewModel.initialize(instance: instance)
    }

    private func handleTestResultChange(_ newValue: Bool?) {
        if newValue == true && saveClicked {
            if let instance = instance {
                viewModel.updateInstance(instance: instance)
            }
        }
    }
}
