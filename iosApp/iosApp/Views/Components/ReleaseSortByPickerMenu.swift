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
            Picker("sort_by", selection: $sortBy) {
                ForEach(ReleaseSortBy.companion.allEntries(), id: \.self) { sort in
                    Text(sort.label()).tag(sort)
                }
            }
            .pickerStyle(.inline)
            
            Section {
                Picker("direction", selection: $sortOrder) {
                    ForEach(Shared.SortOrder.allCases, id: \.self) { order in
                        Label {
                            Text(String(localized: String.LocalizationValue(order.iosText)))
                        } icon: {
                            Image(systemName: order.iosIcon)
                        }
                        .tag(order)
                    }
                }
                .pickerStyle(.inline)
            }
        } label: {
            Image(systemName: "arrow.up.arrow.down")
                .imageScale(.medium)
        }
    }
}
