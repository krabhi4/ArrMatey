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
    
    var body: some View {
        Section {
            ReleaseDownloadButtons(onInteractiveClicked: {
                //todo
            }, automaticSearchEnabled: movie.monitored, onAutomaticClicked: onAutomaticSearch, automaticSearchInProgress: searchIds.contains(movie.id as! Int64))
            
            if let file = movie.movieFile {
                fileArea(file)
            }
            
            extraFilesArea
            
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
            }
            .frame(maxWidth: .infinity)
        }
            
    }
    
    @ViewBuilder
    private func fileArea(_ file: MovieFile) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(file.relativePath)
                .font(.system(size: 18, weight: .medium))
            
            Text(fileInfoLine(file: file))
                .font(.system(size: 14))
            
            if let dateAdded = file.dateAdded?.format(pattern: "MMM d, yyyy") {
                Text(String(localized: LocalizedStringResource("added_on \(dateAdded)")))
                    .font(.system(size: 14))
            }
        }
        .padding(.vertical, 12)
        .padding(.horizontal, 18)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 10, style: .continuous)
                .fill(Color(.systemGroupedBackground))
        )
    }
    
    private var extraFilesArea: some View {
        ForEach(movieExtraFiles, id: \.id) { extraFile in
            VStack(alignment: .leading, spacing: 4) {
                Text(extraFile.relativePath)
                    .font(.system(size: 16, weight: .medium))
                
                Text(extraFile.type.name)
                    .font(.system(size: 14))
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 18)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(Color(.systemGroupedBackground))
            )
        }
    }
    
    private func fileInfoLine(file: MovieFile) -> String {
        let languageName = file.languages.first?.name ?? ""
        let sizeString = file.size.bytesAsFileSizeString()
        let qualityName = file.quality?.quality.name ?? ""
        return [languageName, sizeString, qualityName]
            .filter { !$0.isEmpty }
            .joined(separator: " • ")
    }
    
}
