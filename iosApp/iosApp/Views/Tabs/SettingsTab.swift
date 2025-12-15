//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SettingsTab: View {
    @StateObject var instanceViewModel = InstanceViewModel()
    
    @EnvironmentObject var navigationManager: NavigationManager
    
    var body: some View {
        NavigationStack(path: $navigationManager.settingsPath) {
            SettingsScreen()
                .navigationDestination(for: SettingsRoute.self) { value in
                    switch value {
                    case .newInstance:
                        NewInstanceView()
                    case .dev:
                        DevSettingsScreen()
                    }
                }
        }
        .task {
            await instanceViewModel.refresh()
        }
    }
}
