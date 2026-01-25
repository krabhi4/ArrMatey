//
//  MediaHeaderBanner.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-29.
//

import SwiftUI
import Shared

struct MediaHeaderBanner: UIViewControllerRepresentable {
    let bannerUrl: String?
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return MediaHeaderBannerViewController(bannerUrl: bannerUrl)
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        // left blank
    }
}
