//
//  MediaDetailsHeader.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-29.
//

import Shared
import SwiftUI

struct MediaDetailsHeader: View {
    let item: ArrMedia
    
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        ZStack {
            MediaHeaderBanner(bannerUrl: item.getBanner()?.remoteUrl)
            HStack(alignment: .top, spacing: 12) {
                PosterItem(item: item)
                    .frame(width: 150, height: 220)
                
                VStack(alignment: .leading, spacing: 4) {
                    if let clearLogo = item.getClearLogo()?.remoteUrl {
                        AsyncImage(url: URL(string: clearLogo)) { phase in
                            if let image = phase.image {
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(minHeight: 64)
                                    .background {
                                        if colorScheme == .light {
                                            RoundedRectangle(cornerRadius: 16)
                                                .fill(Color.black.opacity(0.4))
                                                .blur(radius: 5)
                                                .padding(-4)
                                        }
                                    }
                            } else {
                                Color.clear.frame(height: 64)
                            }
                        }
                    } else {
                        Text(item.title)
                            .font(.system(size: 36, weight: .bold))
                            .lineLimit(3)
                            .truncationMode(.tail)
                    }
                    
                    Text([String(item.year), item.runtimeString, item.certification ?? "NA"].joined(separator: " • "))
                        .font(.system(size: 16))
                    
                    Text([item.releasedBy ?? "", item.statusString].joined(separator: " • "))
                        .font(.system(size: 14))
                    
                    Text(item.genres.joined(separator: " • "))
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                    
                }
                .frame(maxWidth: .infinity, alignment: .top)
                
                Spacer()
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 170)
            .padding(.horizontal, 12)
        }
    }
}
