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

    @Published var sortBy: SortBy = .title
    @Published var sortOrder: Shared.SortOrder = .asc
    @Published var filterBy: FilterBy = .all
    @Published var showInfoCardMap: [InstanceType:Bool] = [:]
    @Published var viewTypeMap: [InstanceType:ViewType] = [:]
    
    init() {
        self.preferenceStore = KoinBridge.shared.getPreferencesStore()
        observeFlows()
    }
    
    private func observeFlows() {
        preferenceStore.showInfoCards.observeAsync {
            self.showInfoCardMap = $0.mapValues(\.boolValue)
        }
    }
    
    func setInfoCardVisibility(type: InstanceType, visible: Bool) {
        preferenceStore.setInfoCardVisibility(type: type, value: visible)
    }

    
}
