//
//  InstancePicker.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-17.
//

import SwiftUI
import Shared

struct InstancePickerMenu: View {
    let type: InstanceType
    let instances: [Instance]
    let onChangeInstance: (Instance) -> Void
    
    @EnvironmentObject private var navigationManager: NavigationManager
    
    var body: some View {
        Menu {
            ForEach(instances, id: \.self) { i in
                Button(action: {
                    onChangeInstance(i)
                }) {
                    HStack {
                        Text(i.label)
                        Spacer()
                        if i.selected {
                            Image(systemName: "checkmark")
                                .foregroundColor(.themePrimary)
                        }
                    }
                }
            }
            Divider()
            Button(action: {
                navigationManager.goToNewInstance(of: type)
            }) {
                Label(MR.strings().add_instance.localized(), systemImage: "plus")
            }
        } label: {
            Image(systemName: "externaldrive.connected.to.line.below.fill")
                .imageScale(.medium)
        }
    }
}
