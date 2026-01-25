//
//  EpisodeDetailsHeader.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import Shared
import SwiftUI

struct EpisodeDetailsHeader: View {
    let series: ArrSeries
    let episode: Episode
    
    var body: some View {
        ZStack {
            MediaHeaderBanner(bannerUrl: episode.getBanner()?.remoteUrl)
            HStack(alignment: .top, spacing: 12) {
                if let url = episode.getBanner()?.remoteUrl {
                    AsyncImage(url: URL(string: url)) { image in
                        image.image?
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                    }
                        .frame(width: 200, height: 100)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                        .background(.clear)
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(episode.displayTitle)
                        .font(.system(size: 32, weight: .bold))
                        .lineLimit(3)
                        .truncationMode(.tail)
                    Text(series.title)
                        .font(.system(size: 18))
                    
                    Text(statusRow)
                        .font(.system(size: 16))
                }
                .frame(alignment: .top)
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 170)
            .padding(.horizontal, 12)
        }
    }
    
    private var statusRow: String {
        [
            episode.seasonEpLabel,
            episode.runtimeString,
            episode.formatAirDateUtc()
        ]
            .compactMap { $0 }
            .joined(separator: " • ")
            .breakable()
    }
}
