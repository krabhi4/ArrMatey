//
//  MediaDetailsView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-10.
//

import SwiftUI
import Shared

struct MediaDetailsView: View {
    let item: ArrMedia
    let isActive: Bool
    
    var body: some View {
        if let series = item as? ArrSeries {
            SeriesDetailsView(item: series, isActive: isActive)
        } else if let movie = item as? ArrMovie {
            MovieDetailsView(item: movie)
        }
    }
}
