//
//  MoviesTab.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import Foundation
import SwiftUI
import Shared

struct MoviesTab: View {
    @EnvironmentObject private var navigationManager: NavigationManager
    
    var body: some View {
        NavigationStack(path: $navigationManager.moviePath) {
            ArrTab(type: .radarr)
                .navigationDestination(for: MediaRoute.self) { value in
                    switch value {
                    case .details(let id):
                        MediaDetailsScreen(id: id, type: .radarr)
                    }
                }
        }
    }
}
