//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SeriesTab: View {
    @EnvironmentObject private var navigationManager: NavigationManager
    
    @StateObject private var seriesViewModel = ArrMediaViewModelS(type: .sonarr)
    
    var body: some View {
        NavigationStack(path: $navigationManager.seriesPath) {
            ArrTab(type: .sonarr, viewModel: seriesViewModel)
                .navigationDestination(for: MediaRoute.self) { value in
                    destination(for: value)
                }
        }
    }
    
    @ViewBuilder
    private func destination(for route: MediaRoute) -> some View {
        switch route {
        case .details(let id):
            MediaDetailsScreen(id: id, type: .sonarr)
        case .search(let query):
            MediaSearchScreen(query: query, type: .sonarr)
        case .preview(let json):
            MediaPreviewScreen(json: json, type: .sonarr)
        case .seriesReleases(let seriesId, let seasonNumber, let episodeId):
            let releaseParams = ReleaseParamsSeries(seriesId: seriesId?.asKotlinLong, seasonNumber: seasonNumber?.asKotlinInt, episodeId: episodeId?.asKotlinLong)
            let defaultFilter: ReleaseFilterBy = if episodeId != nil { .singleEpisode } else { .seasonPack }
            InteractiveSearchScreen(type: .sonarr, releaseParams: releaseParams, defaultFilter: defaultFilter)
        case .episodeDetails(let seriesJson, let episodeJson):
            EpisodeDetailsScreen(seriesJson: seriesJson, episodeJson: episodeJson)
            
        // unused
        case .movieFiles(_):
            EmptyView()
        case .movieRelease(_):
            EmptyView()
        }
    }
}
