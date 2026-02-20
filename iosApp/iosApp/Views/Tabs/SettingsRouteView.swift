//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SettingsRouteView: View {
    let route: SettingsRoute

    var body: some View {
        switch route {
        case .newInstance(let initialType):
            NewInstanceView(initialType: initialType)
        case .dev:
            DevSettingsScreen()
        case .editInstance(let id):
            EditInstanceScreen(id: id)
        case .navigationConfig:
            TabConfigurationScreen()
        }
    }
}
