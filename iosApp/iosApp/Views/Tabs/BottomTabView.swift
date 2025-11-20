//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import Shared
import SwiftUI

struct BottomTabView: View {
    var tabItem: TabItem

    var body: some View {
        switch tabItem {
        case .settings: SettingsTab()
        case .shows: SeriesTab()
        default: VStack{}
        }
    }
}