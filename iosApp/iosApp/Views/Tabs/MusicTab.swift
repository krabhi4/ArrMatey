//
//  MusicTab.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-12.
//

//import Foundation
import SwiftUI
import Shared

struct MusicTab: View {
    @EnvironmentObject private var navigationManager: NavigationManager
    
    @StateObject private var musicViewModel = ArrMediaViewModelS(type: .lidarr)
    
    var body: some View {
        NavigationStack(path: $navigationManager.musicPath) {
            ArrTab(type: .lidarr, viewModel: musicViewModel)
                .navigationDestination(for: MediaRoute.self) { value in
                    destination(for: value)
                }
        }
    }
    
    @ViewBuilder
    private func destination(for route: MediaRoute) -> some View {
        switch route {
        case .details(let id):
            MediaDetailsScreen(id: id, type: .lidarr)
        case .search(let query):
            MediaSearchScreen(query: query, type: .lidarr)
        case .preview(let json):
            MediaPreviewScreen(json: json, type: .lidarr)
        case .albumReleases(let albumId, let artistId):
            let releaseParams = ReleaseParamsAlbum(albumId: albumId, artistId: artistId?.asKotlinLong)
            InteractiveSearchScreen(type: .lidarr, releaseParams: releaseParams)
            
        // unused
        case .movieFiles(_):
            EmptyView()
        case .movieRelease(_):
            EmptyView()
        case .seriesReleases(_, _, _):
            EmptyView()
        case .episodeDetails(_, _):
            EmptyView()
        }
    }
}
