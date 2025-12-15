//
//  SettingsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

struct SettingsScreen: View {
    @StateObject var instanceViewModel = InstanceViewModel()
    
    @EnvironmentObject private var navigationManager: NavigationManager
    
    var body: some View {
        Form {
            Section {
                ForEach(instanceViewModel.instances, id: \.self) { instance in
                    HStack(spacing: 24){
                        SVGImageView(filename: instance.type.iconKey)
                            .frame(width: 32, height: 32)
                        VStack(alignment: .leading, spacing: 1) {
                            Text(instance.label ?? instance.type.name)
                                .font(.system(size: 18, weight: .medium))
                            Text(instance.url)
                                .font(.system(size: 16))
                        }
                    }
                }
                Button(LocalizedStringResource("add_instance")) {
                    navigationManager.go(to: .newInstance)
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
