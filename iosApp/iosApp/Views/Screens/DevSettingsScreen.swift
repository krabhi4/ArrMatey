//
//  DevSettingsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-06.
//

import Shared
import SwiftUI

struct DevSettingsScreen: View {
    
    @Environment(\.dismiss) var dismiss
    
    @ObservedObject var preferences: PreferencesViewModel = PreferencesViewModel()
    
    var body: some View {
        Form {
            Section {
                ForEach(InstanceType.companion.allValue(), id: \.self) { instanceType in
                    Toggle("Show \(instanceType.name) info card", isOn: Binding(
                        get: { preferences.showInfoCardMap[instanceType] ?? true},
                        set: { newValue in
                            preferences.setInfoCardVisibility(type: instanceType, visible: newValue)
                        }
                    ))
                }
                
                Toggle("Enable activity polling", isOn: Binding(
                    get: { preferences.enableAcitivityPolling },
                    set: { _ in preferences.toggleAcitivityPolling() }
                ))
                
                Picker("HTTP Logging Level", selection: Binding(
                    get: { preferences.logLevel },
                    set: { level in preferences.setLoggingLevel(level)}
                )) {
                    ForEach(LoggerLevel.companion.entries(), id: \.self) { level in
                        Text(level.name).tag(level)
                    }
                }
            }
        }
    }
}
