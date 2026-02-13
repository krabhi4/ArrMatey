//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import Shared
import SwiftUI

struct BottomTabView: View {
    var tabItem: TabItem

    var body: some View {
        NavigationStack {
            switch tabItem {
            case .shows: SeriesTab()
            case .movies: MoviesTab()
            case .music: MusicTab()
            case .activity: ActivityTab()
            case .calendar: CalendarTab()
            case .settings: SettingsTab()
            }
        }
    }
}
