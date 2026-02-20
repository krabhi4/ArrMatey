//
//  SettingsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

struct SettingsScreen: View {
    
    @Environment(\.openURL) private var openURL
    @EnvironmentObject private var navigationManager: NavigationManager
    
    @ObservedObject private var viewModel = MoreScreenViewModelS()
    
    @State private var showLibrariesSheet: Bool = false
    
    private var instances: [Instance] {
        viewModel.instances
    }
    
    var body: some View {
        Form {
            Section {
                ForEach(instances, id: \.self) { instance in
                    NavigationLink(value: SettingsRoute.editInstance(instance.id)) {
                        HStack(spacing: 24){
                            SVGImageView(filename: instance.type.iconKey)
                                .frame(width: 32, height: 32)
                            VStack(alignment: .leading, spacing: 1) {
                                Text(instance.label)
                                    .font(.system(size: 18, weight: .medium))
                                Text(instance.url)
                                    .font(.system(size: 16))
                            }
                        }
                    }
                }
                NavigationLink(value: SettingsRoute.newInstance()) {
                    Text(MR.strings().add_instance.localized())
                        .foregroundColor(.themePrimary)
                }
            } header: {
                Text(MR.strings().instances.localized())
            }
            
            Section {
                NavigationLink(value: SettingsRoute.navigationConfig) {
                    HStack(spacing: 24) {
                        Image(systemName: "location.north.fill")
                            .foregroundColor(.themePrimary)
                            .frame(width: 32, height: 32)
                        
                        VStack(alignment: .leading, spacing: 1) {
                            Text(MR.strings().navigation_bar_configuration.localized())
                                .font(.system(size: 18, weight: .medium))
                        }
                    }
                }
            }
            
            AboutCard(
                onGitHubClick: { if let url = URL(string: MR.strings().app_link.localized()) {
                    openURL(url)
                } },
                onDonateClick: { if let url = URL(string: MR.strings().bmac_link.localized()) {
                    openURL(url)
                } },
                onLibrariesClick: { showLibrariesSheet = true }
            )
            
            Section {
                if isDebug() {
                    Button("Dev settings") {
                        navigationManager.go(to: .dev)
                    }
                }
            }
        }
        .navigationTitle(MR.strings().settings.localized())
        .sheet(isPresented: $showLibrariesSheet) {
            LibrariesSheet()
        }
    }
}
