//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SeriesTab: View {
    @EnvironmentObject private var navigationManager: NavigationManager
    
    var body: some View {
        NavigationStack(path: $navigationManager.seriesPath) {
            ArrTab(type: .sonarr)
                .navigationDestination(for: MediaRoute.self) { value in
                    switch value {
                    case .details(let id):
                        MediaDetailsScreen(id: id, type: .sonarr)
                    }
                }
        }
    }
}
