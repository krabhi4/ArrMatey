//
//  ReleaseSortByPickerMenu.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import Shared
import SwiftUI

struct ReleaseSortByPickerMenu: View {
    
    @Binding var sortBy: ReleaseSortBy
    @Binding var sortOrder: Shared.SortOrder
    
    var body: some View {
        Menu {
            ForEach(ReleaseSortBy.allCases, id: \.self) { sortOption in
                Button(action: {
                    if sortBy == sortOption {
                        sortOrder = (sortOrder == .asc) ? .desc : .asc
                    } else {
                        sortBy = sortOption
                    }
                }) {
                    if sortBy == sortOption {
                        Label(sortOption.resource.localized(), systemImage: sortOrder == .asc ? "chevron.up" : "chevron.down")
                    } else {
                        Text(sortOption.resource.localized())
                    }
                }
            }
        } label: {
            Image(systemName: "arrow.up.arrow.down")
                .imageScale(.medium)
        }
    }
}
