//
//  MovieFilesView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import SwiftUI
import Shared

struct MovieFilesView: View {
    let movie: ArrMovie
    let movieExtraFiles: [ExtraFile]
    let searchIds: Set<Int64>
    let searchResult: Bool?
    let onAutomaticSearch: () -> Void
    
    @EnvironmentObject private var navigation: NavigationManager
    
    var body: some View {
        Section {
            ReleaseDownloadButtons(onInteractiveClicked: {
                if let id = movie.id?.int64Value {
                    navigation.go(to: .movieRelease(id), of: .radarr)
                }
            }, automaticSearchEnabled: movie.monitored, onAutomaticClicked: onAutomaticSearch, automaticSearchInProgress: searchIds.contains(movie.id as! Int64))
            
            if let file = movie.movieFile {
                MovieFileCard(file: file)
            }
            
            MovieExtraFilesView(extraFiles: movieExtraFiles)
            
            if movie.movieFile == nil && movieExtraFiles.isEmpty {
                Text(String(localized: LocalizedStringResource("no_files")))
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .multilineTextAlignment(.center)
            }
        } header: {
            HStack(alignment: .center) {
                Text(String(localized: LocalizedStringResource("files")))
                    .font(.system(size: 20, weight: .bold))
                Spacer()
                Text(String(localized: LocalizedStringResource("history")))
                    .font(.system(size: 16))
                    .foregroundColor(.accentColor)
                    .onTapGesture {
                        let json = movie.toJson()
                        navigation.go(to: .movieFiles(json), of: .radarr)
                    }
            }
            .frame(maxWidth: .infinity)
        }
            
    }
    
}
