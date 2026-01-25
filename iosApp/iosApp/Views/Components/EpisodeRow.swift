//
//  EpisodeRow.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import SwiftUI
import Shared

struct EpisodeRow: View {
    let episode: Episode
    let onToggleEpisodeMonitor: (Episode) -> Void
    let onAutomaticSearch: () -> Void
    let automaticSearchDisabled: Bool
    let onClicked: () -> Void
    
    @EnvironmentObject private var navigation: NavigationManager
    
    private var statusString: String? {
        episode.episodeFile?.qualityName ??
        (episode.airDate?.isTodayOrAfter() == true ? String(localized: LocalizedStringResource("unaired")) : nil)
    }
    
    var body: some View {
        Button(action: onClicked) {
            HStack(spacing: 8) {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 0) {
                        Text("\(episode.episodeNumber). ")
                            .font(.system(size: 16))
                            .fontWeight(.medium)
                            .foregroundColor(.accentColor)
                        
                        Text(episode.title ?? "")
                            .font(.system(size: 16))
                            .fontWeight(.medium)
                            .lineLimit(1)
                        
                        if let finaleType = episode.finaleType {
                            Text(" • \(finaleType.label)")
                                .font(.system(size: 12))
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    HStack(spacing: 4) {
                        if let statusString = statusString {
                            Text(statusString)
                                .font(.system(size: 14))
                                .italic(episode.airDate?.isTodayOrAfter() == true)
                        } else {
                            Text(String(localized: LocalizedStringResource("missing")))
                                .font(.system(size: 14))
                                .foregroundColor(.red)
                                .italic()
                        }
                        
                        let airDateText = " • \(episode.formatAirDateUtc() ?? "")"
                        Text(airDateText)
                            .font(.system(size: 14))
                            .fontWeight(episode.airDate?.isToday() == true ? .medium : .regular)
                            .foregroundColor(episode.airDate?.isToday() == true ? .accentColor : .primary)
                    }
                }
                
                Spacer()
                
                Image(systemName: "person.fill")
                    .onTapGesture {
                        let route: MediaRoute = .seriesReleases(episodeId: episode.id)
                        navigation.go(to: route, of: .sonarr)
                    }
                
                Image(systemName: "magnifyingglass")
                    .onTapGesture {
                        onAutomaticSearch()
                    }
                    .disabled(automaticSearchDisabled)
                
                Image(systemName: episode.monitored ? "bookmark.fill" : "bookmark")
                    .onTapGesture {
                        onToggleEpisodeMonitor(episode)
                    }
            }
        }
    }
}
