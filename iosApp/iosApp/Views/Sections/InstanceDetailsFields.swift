//
//  InstanceDetailsFields.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-18.
//

import SwiftUI
import Shared

struct InstanceDetailsFields: View {
    @StateObject var viewModel: AddInstanceViewModel
    let type: InstanceType
    let urlPlaceholder: String
    
    var body: some View {
        HStack(spacing: 24) {
            Text(LocalizedStringResource("label")).layoutPriority(2)
            TextField(text: $viewModel.instanceLabel, prompt: Text(type.name)) { EmptyView() }
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
}
