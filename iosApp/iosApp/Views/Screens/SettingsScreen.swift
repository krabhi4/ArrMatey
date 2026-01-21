//
//  SettingsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

struct SettingsScreen: View {
    
    @EnvironmentObject private var navigationManager: NavigationManager
    
    @ObservedObject private var viewModel = MoreScreenViewModelS()
    
    private var instances: [Instance] {
        viewModel.instances
    }
    
    var body: some View {
        Form {
            Section {
                ForEach(instances, id: \.self) { instance in
                    HStack(spacing: 24){
                        SVGImageView(filename: instance.type.iconKey)
                            .frame(width: 32, height: 32)
                        VStack(alignment: .leading, spacing: 1) {
                            Text(instance.label)
                                .font(.system(size: 18, weight: .medium))
                            Text(instance.url)
                                .font(.system(size: 16))
                        }
                        Spacer()
                        Image(systemName: "chevron.right")
                    }
                    .onTapGesture {
                        navigationManager.go(to: .editInstance(instance.id))
                    }
                }
                Button(LocalizedStringResource("add_instance")) {
                    navigationManager.go(to: .newInstance())
                }
            } header: {
                Text(LocalizedStringResource("instances"))
            }
            if isDebug() {
                Button("Dev settings") {
                    navigationManager.go(to: .dev)
                }
            }
        }
        .navigationTitle(LocalizedStringResource("settings"))
    }
}
