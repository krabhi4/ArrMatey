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
    
    var body: some View {
        Section {
            if let file = movie.movieFile {
                VStack(alignment: .leading, spacing: 8) {
                    Text(file.relativePath)
                        .font(.system(size: 18, weight: .medium))
                    
                    Text(fileInfoLine(file: file))
                        .font(.system(size: 14))
                    
                    let dateAdded = file.dateAdded.format(pattern: "MMM d, yyyy")
                    Text(String(localized: LocalizedStringResource("added_on \(dateAdded)")))
                        .font(.system(size: 14))
//                    Text(String(localized: LocalizedStringResource("added_on ")))
                }
                .padding(.vertical, 12)
                .padding(.horizontal, 18)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .fill(Color(.systemGroupedBackground))
                )
            } else {
                Text(String(localized: LocalizedStringResource("no_files")))
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .multilineTextAlignment(.center)
            }
        }
    }
    
    private func fileInfoLine(file: MovieFile) -> String {
        let languageName = file.languages.first?.name ?? ""
        let sizeString = file.size.bytesAsFileSizeString()
        let qualityName = file.quality.quality.name
        return [languageName, sizeString, qualityName]
            .filter { !$0.isEmpty }
            .joined(separator: " • ")
    }
}
