//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SettingsTab: View {

    @EnvironmentObject private var navigationManager: NavigationManager
    
    var body: some View {
        NavigationStack(path: $navigationManager.settingsPath) {
            SettingsScreen()
                .navigationDestination(for: SettingsRoute.self) { value in
                    switch value {
                    case .newInstance(let initialType):
                        NewInstanceView(initialType: initialType)
                    case .dev:
                        DevSettingsScreen()
                    case .editInstance(let id):
                        EditInstanceScreen(id: id)
                    }
                }
        }
    }
}
