//
//  SortByPickerView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import Shared
import SwiftUI

struct SortByPickerMenu: View {
    private let type: InstanceType
    
    private let changeSortBy: (SortBy) -> Void
    private let changeSortOrder: (Shared.SortOrder) -> Void
    
    private let sortByOptions: [SortBy]
    
    @State private var sortedBy: SortBy
    @State private var sortOrder: Shared.SortOrder
    
    init(
        type: InstanceType,
        sortBy: SortBy,
        sortOrder: Shared.SortOrder,
        changeSortBy: @escaping (SortBy) -> Void,
        changeSortOrder: @escaping (Shared.SortOrder) -> Void,
        limitToLookup: Bool = false
    ) {
        self.type = type
        self.sortedBy = sortBy
        self.sortOrder = sortOrder
        self.changeSortBy = changeSortBy
        self.changeSortOrder = changeSortOrder
        
        self.sortByOptions = limitToLookup ? SortBy.companion.lookupEntries() : SortBy.companion.typeEntries(type: type)
    }
    
    var body: some View {
        Menu {
            ForEach(sortByOptions, id: \.self) { sortOption in
                Button(action: {
                    if sortedBy == sortOption {
                        changeSortOrder((sortOrder == .asc) ? .desc : .asc)
                    } else {
                        changeSortBy(sortOption)
                    }
                }) {
                    if sortedBy == sortOption {
                        Label(sortOption.resource.localized(), systemImage: sortOrder == .asc ? "chevron.up" : "chevron.down")
                    } else {
                        Text(sortOption.resource.localized())
                    }
                }
            }
        } label: {
            Label(sortedBy.resource.localized(), systemImage: "arrow.up.arrow.down")
        }
        .onChange(of: sortedBy, { _, newValue in
            changeSortBy(newValue)
        })
        .onChange(of: sortOrder, { _, newValue in
            changeSortOrder(newValue)
        })
    }
}
