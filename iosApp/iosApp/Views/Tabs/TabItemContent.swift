//
//  TabItemContent.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-13.
//

import SwiftUI
import Shared

struct TabItemContent: View {
    let tabItem: TabItem
    @EnvironmentObject var navigationManager: NavigationManager

    var body: some View {
        Group {
            switch tabItem {
            case .shows: SeriesTab()
            case .movies: MoviesTab()
            case .music: MusicTab()
            case .activity: ActivityTab()
            case .calendar: CalendarTab()
            case .settings: SettingsScreen()
                    .navigationDestination(for: SettingsRoute.self) { route in
                        SettingsRouteView(route: route)
                    }
            }
        }
        .navigationTitle(LocalizedStringKey(tabItem.resource.localized()))
    }
}
