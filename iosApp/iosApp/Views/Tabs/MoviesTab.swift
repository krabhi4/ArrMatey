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
                    destination(for: value)
                }
        }
    }
    
    @ViewBuilder
    private func destination(for route: MediaRoute) -> some View {
        switch route {
        case .details(let id):
            MediaDetailsScreen(id: id, type: .radarr)
        case .search(let query):
            MediaSearchScreen(query: query, type: .radarr)
        case .preview(let json):
            MediaPreviewScreen(json: json, type: .radarr)
        case .movieFiles(let json):
            MovieFilesScreen(json: json)
        case .movieRelease(let id):
            let releaseParams = ReleaseParamsMovie(movieId: id)
            InteractiveSearchScreen(type: .radarr, canFilter: true, releaseParams: releaseParams)
        case .seriesReleases(_, _, _):
            EmptyView() // should never happen on this tab
        }
    }
}
