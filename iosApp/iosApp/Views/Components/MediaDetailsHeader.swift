//
//  MediaDetailsHeader.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-29.
//

import Shared
import SwiftUI

struct MediaDetailsHeader: View {
    let item: AnyArrMedia
    
    var body: some View {
        ZStack {
            MediaHeaderBanner(item: item)
            HStack(alignment: .top, spacing: 12) {
                PosterItemView(
                    item: item,
                    onItemClick: nil
                )
                .frame(width: 150, height: 220)
                .clipped()
                .clipShape(RoundedRectangle(cornerRadius: 10))
                .background(.clear)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(item.title)
                        .font(.system(size: 36, weight: .bold))
                        .lineLimit(3)
                        .truncationMode(.tail)
                    
                    Text([String(item.year), item.runtimeString, item.certification ?? "NA"].joined(separator: " • "))
                        .font(.system(size: 16))
                    
                    Text([item.releasedBy ?? "", item.statusString].joined(separator: " • "))
                        .font(.system(size: 14))
                    
                    Text(item.genres.joined(separator: " • "))
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                    
                }
                .frame(alignment: .top)
                
                Spacer()
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 170)
            .padding(.horizontal, 12)
        }
    }
}
