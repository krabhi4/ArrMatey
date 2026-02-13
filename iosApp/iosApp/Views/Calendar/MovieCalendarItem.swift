//
//  MovieCalendarItem.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-09.
//

import SwiftUI
import Shared

struct MovieCalendarItem: View {
    let movie: ArrMovie
    
    private var statusIcon: String? {
        if movie.isDownloaded {
            return "checkmark.circle.fill"
        } else if !movie.monitored {
            return "bookmark"
        } else if movie.isWaiting {
            return "clock.fill"
        } else if movie.monitored {
            return "bookmark.fill"
        }
        return nil
    }
    
    private var releaseTypeText: String? {
        if movie.inCinemas != nil {
            return "In Cinemas"
        } else if movie.digitalRelease != nil {
            return "Digital Release"
        }
        return nil
    }
    
    var body: some View {
        HStack(spacing: 12) {
            PosterItem(item: movie)
                .frame(width: 50)
            
            VStack(alignment: .leading, spacing: 6) {
                Text(movie.title)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                if let releaseType = releaseTypeText {
                    HStack(spacing: 8) {
                        Text(releaseType)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            
            Spacer()
            
            if let icon = statusIcon {
                Image(systemName: icon)
                    .font(.system(size: 20))
                    .foregroundColor(.primary)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
    }
}
