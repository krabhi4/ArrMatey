//
//  BannerView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-10.
//

import SwiftUI
import Shared

struct BannerView: View {
    let item: ArrMedia
    
    var body: some View {
        if let bannerUrl = item.getBanner()?.remoteUrl {
            AsyncImage(url: URL(string: bannerUrl)) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .blur(radius: 2)
            } placeholder: {
                Color.gray.opacity(0.3)
            }
        } else {
            Color.clear
        }
    }
}
