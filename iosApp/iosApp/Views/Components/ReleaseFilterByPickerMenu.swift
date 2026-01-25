//
//  ReleaseFilterByPickerMenu.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-24.
//

import Shared
import SwiftUI

struct ReleaseFilterByPickerMenu: View {
    @Binding var filterBy: ReleaseFilterBy
    
    var body: some View {
        Menu {
            Picker("Filter By", selection: $filterBy) {
                ForEach(ReleaseFilterBy.companion.allEntries(), id: \.self) { filter in
                    Text(filter.label()).tag(filter)
                }
            }
            .pickerStyle(.inline)
        } label: {
            Image(systemName: "line.3.horizontal.decrease")
                .imageScale(.medium)
        }
    }
    
}
