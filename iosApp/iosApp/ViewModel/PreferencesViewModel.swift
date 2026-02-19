//
//  PreferencesViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-05.
//

import SwiftUI
import Shared

@MainActor
class PreferencesViewModel: ObservableObject {
    private let preferenceStore: PreferencesStore

    @Published var showInfoCardMap: [InstanceType:Bool] = [:]
    @Published var enableAcitivityPolling: Bool = true
    @Published var logLevel: LoggerLevel = .headers
    
    init() {
        self.preferenceStore = KoinBridge.shared.getPreferencesStore()
        observeFlows()
    }
    
    private func observeFlows() {
        preferenceStore.showInfoCards.observeAsync {
            self.showInfoCardMap = $0.mapValues(\.boolValue)
        }
        preferenceStore.enableActivityPolling.observeAsync {
            self.enableAcitivityPolling = $0.boolValue
        }
        preferenceStore.httpLogLevel.observeAsync { self.logLevel = $0 }
    }
    
    func setInfoCardVisibility(type: InstanceType, visible: Bool) {
        preferenceStore.setInfoCardVisibility(type: type, value: visible)
    }

    func toggleAcitivityPolling() {
        preferenceStore.toggleActivityPolling()
    }
    
    func setLoggingLevel(_ level: LoggerLevel) {
        preferenceStore.setLogLevel(level: level)
    }
    
}
