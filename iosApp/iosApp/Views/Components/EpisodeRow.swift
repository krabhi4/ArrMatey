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
    let viewModel: SonarrViewModel
    
    private var statusString: String? {
        episode.episodeFile?.qualityName ??
        (episode.airDate?.isTodayOrAfter() == true ? String(localized: LocalizedStringResource("unaired")) : nil)
    }
    
    var body: some View {
        HStack(spacing: 8) {
            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 0) {
                    Text("\(episode.episodeNumber). ")
                        .font(.system(size: 16))
                        .fontWeight(.medium)
                        .foregroundColor(.accentColor) // primary color
                    
                    Text(episode.title ?? "")
                        .font(.system(size: 16))
                        .fontWeight(.medium)
                    
                    if let finaleType = episode.finaleType {
                        Text(" • \(finaleType.label)")
                            .font(.system(size: 12))
                            .foregroundColor(.secondary)
                    }
                }
                
                // Status row
                HStack(spacing: 4) {
                    if let statusString = statusString {
                        Text(statusString)
                            .font(.system(size: 14))
                    } else {
                        Text(String(localized: LocalizedStringResource("missing")))
                            .font(.system(size: 14))
                            .foregroundColor(.red)
                    }
                    
                    let airDateText = " • \(episode.formatAirDateUtc() ?? "")"
                    Text(airDateText)
                        .font(.system(size: 14))
                        .fontWeight(episode.airDate?.isToday() == true ? .medium : .regular)
                        .foregroundColor(episode.airDate?.isToday() == true ? .accentColor : .primary)
                }
            }
            
            Spacer()
            
            Image(systemName: episode.monitored ? "bookmark.fill" : "bookmark")
                .onTapGesture {
                    Task {
                        await viewModel.toggleEpisodeMonitor(episodeId: episode.id)
                    }
                }
        }
    }
}
