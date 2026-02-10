//
//  ActivityFilterMenu.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-10.
//

import Shared
import SwiftUI

struct ActivityFilterMenu: View {
    @Binding var sortBy: QueueSortBy
    @Binding var sortOrder: Shared.SortOrder
    @Binding var instanceId: Int64?
    let instances: [Instance]
    
    private var instancePickerTitle: String {
        guard let id = instanceId else { return MR.strings().instances.localized() }
        return instances.first(where: { $0.id == id })?.label ?? MR.strings().all.localized()
    }
    
    var body: some View {
        Menu {
            Menu {
                Picker(instancePickerTitle, selection: $instanceId) {
                    Text(MR.strings().all.localized()).tag(nil as Int64?)
                    ForEach(instances, id: \.id) { instance in
                        Text(instance.label).tag(instance.id)
                    }
                }
                .pickerStyle(.inline)
            } label: {
                Label(instancePickerTitle, systemImage: "externaldrive.connected.to.line.below.fill")
            }
            
            Section {
                ForEach(QueueSortBy.allCases, id: \.self) { sortOption in
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
            }
        } label: {
            Image(systemName: "line.3.horizontal.decrease")
                .imageScale(.medium)
        }
    }
}
