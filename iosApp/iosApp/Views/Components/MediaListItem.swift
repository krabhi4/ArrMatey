//
//  MediaListItem.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-10.
//

import SwiftUI
import Shared

struct MediaItemView<T: ArrMedia>: View {
    let item: T
    let isActive: Bool
    
    init(item: T, isActive: Bool = false) {
        self.item = item
        self.isActive = isActive
    }
    
    private var itemTitle: String {
        var result = item.title
        if let year = item.year {
            if !item.title.contains(String(describing: year)) {
                result += " (\(year))"
            }
        }
        return result
    }
    
    var body: some View {
        ZStack {
            BannerView(item: item)
                .frame(height: 100)
            
            Color.black.opacity(0.5)
            
            HStack(spacing: 18) {
                AsyncImage(url: URL(string:item.getPoster()?.remoteUrl ?? "")) { image in
                    image
                        .resizable()
                        .aspectRatio(0.675, contentMode: .fit)
                } placeholder: {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .aspectRatio(0.675, contentMode: .fit)
                }
                .frame(height: 75)
                .cornerRadius(12)
                
                VStack(alignment: .leading, spacing: 0) {
                    Text(itemTitle)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                        .lineLimit(1)
                    
                    MediaDetailsView(item: item, isActive: isActive)
                }
            }
            .padding(12)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 100)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 4)
    }
}
