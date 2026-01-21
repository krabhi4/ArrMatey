//
//  NavigationManager.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-11.
//

import SwiftUI
import Shared

class NavigationManager: ObservableObject {
    @Published var settingsPath = NavigationPath()
    @Published var seriesPath = NavigationPath()
    @Published var moviePath = NavigationPath()
    
    @Published var selectedTab: TabItem = .shows
    
    func go(to route: MediaRoute, of type: InstanceType) {
        switch type {
        case .sonarr:
            seriesPath.append(route)
        case .radarr:
            moviePath.append(route)
        }
    }
    
    func replaceCurrent(with route: MediaRoute, for type: InstanceType) {
        switch type {
        case .sonarr:
            seriesPath.removeLast()
            seriesPath.append(route)
        case .radarr:
            moviePath.removeLast()
            moviePath.append(route)
        }
    }
    
    func go(to route: SettingsRoute) {
        settingsPath.append(route)
    }
}

enum MediaRoute: Hashable {
    case details(Int64)
    case search(String)
    case preview(String)
}

enum SettingsRoute : Hashable {
    case newInstance(_ : InstanceType = .sonarr)
    case dev
    case editInstance(Int64)
}
