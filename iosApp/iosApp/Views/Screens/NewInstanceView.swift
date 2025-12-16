//
//  NewInstanceView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

struct NewInstanceView: View {
    
    @Environment(\.openURL) var openURL
    @Environment(\.dismiss) var dismiss
    
    @StateObject private var viewModel = AddInstanceViewModel()
    @State private var instanceType: InstanceType = .sonarr
    
    var urlPlaceholder: String {
        String(localized: LocalizedStringResource("host_placeholder")) + String(instanceType.defaultPort)
    }
    
    var showInfoCard: Bool {
        viewModel.infoCardMap[instanceType]?.boolValue ?? true
    }
    
    var body: some View {
        Form {
            if showInfoCard {
                VStack(spacing: 8) {
                    HStack {
                        SVGImageView(filename: instanceType.iconKey)
                            .frame(width: 24, height: 24)
                        Text(String(instanceType.name))
                            .font(.system(size: 18, weight: .medium))
                        Spacer()
                        Button(action: {
                            viewModel.dismissInfoCard(instanceType: instanceType)
                        }) {
                            Image(systemName: "xmark")
                                .font(.system(size: 18, weight: .medium))
                                .foregroundStyle(.secondary)
                                .frame(width: 32, height: 32)
                        }
                        .buttonStyle(.plain)
                    }
                    Text(LocalizedStringResource(stringLiteral: instanceType.descriptionKey))
                        .font(.system(size: 14))
                    HStack(spacing: 8) {
                        Button(LocalizedStringResource("github"), action: {
                            if let url = URL(string: instanceType.github) {
                                openURL(url)
                            }
                        })
                        .frame(maxWidth: .infinity)
                        
                        Button(LocalizedStringResource("website"), action: {
                            if let url = URL(string: instanceType.website) {
                                openURL(url)
                            }
                        })
                        .frame(maxWidth: .infinity)
                    }
                    .padding(.horizontal)
                    .alignmentGuide(VerticalAlignment.center) { d in d[VerticalAlignment.center]}
                }
            }
            
            Section {
                Picker(LocalizedStringResource("instance_type"), selection: $instanceType) {
                    ForEach(InstanceType.companion.allValue(),  id: \.self) { type in
                        Text(String(localized: LocalizedStringResource(stringLiteral: type.name	)))
                            .tag(type)
                    }
                }
                .tint(.primary)
                
                HStack(spacing: 24) {
                    Text(LocalizedStringResource("label")).layoutPriority(2)
                    TextField(text: $viewModel.instanceLabel, prompt: Text(instanceType.name)) { EmptyView() }
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
            } footer: {
                Text(LocalizedStringKey("host_description \(String(instanceType.name))"))
            }
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
            
            Section {
                Toggle(LocalizedStringResource("slow_instance"), isOn: $viewModel.isSlowInstance)
                    .onChange(of: viewModel.isSlowInstance) { _, newValue in
                        viewModel.setIsSlowInstance(newValue)
                    }
                HStack(spacing: 24) {
                    Text(LocalizedStringResource("custom_timeout_seconds"))
                        .foregroundStyle(viewModel.isSlowInstance ? Color.primary.opacity(1.0) : Color.primary.opacity(0.3))
                    TextField(text: customTimeoutTextBinding, prompt: Text("300")) { EmptyView() }
                        .multilineTextAlignment(.trailing)
                        .keyboardType(.numberPad)
                        .onChange(of: viewModel.customTimeout) { _, newValue in
                            viewModel.setCustomTimeout(newValue)
                        }
                        .disabled(!viewModel.isSlowInstance)
                }
            }
        }
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button(LocalizedStringResource("save")) {
                    Task {
                        viewModel.saveInstance(instanceType: instanceType)
                        dismiss()
                    }
                }
                .disabled(!viewModel.saveButtonEnabled)
            }
        }
    }
    
    private var customTimeoutTextBinding: Binding<String> {
        Binding(
            get: {
                if let value = viewModel.customTimeout {
                    return String(value)
                } else {
                    return ""
                }
            },
            set: { newValue in
                let trimmed = newValue.trimmingCharacters(in: .whitespacesAndNewlines)
                if trimmed.isEmpty {
                    viewModel.setCustomTimeout(nil)
                } else if let intValue = Int64(trimmed) {
                    viewModel.setCustomTimeout(intValue)
                } else {
                    // Optional: decide what to do with invalid input.
                    // For now, ignore or keep previous value.
                }
            }
        )
    }
}
