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

    private let preferenceStore = PreferencesStore()

    @Published var sortBy: SortBy = .title
    @Published var sortOrder: Shared.SortOrder = .asc
    @Published var filterBy: FilterBy = .all
    @Published var showInfoCardMap: [InstanceType:Bool] = [:]
    @Published var viewTypeMap: [InstanceType:ViewType] = [:]
    
    init() {
        observeFlows()
    }
    
    private func observeFlows() {
        Task {
            for await value in preferenceStore.sortBy {
                self.sortBy = value
            }
        }
        Task {
            for await value in preferenceStore.sortOrder {
                self.sortOrder = value
            }
        }
        Task {
            for await value in preferenceStore.filterBy {
                self.filterBy = value
            }
        }
        Task {
            for await map in preferenceStore.showInfoCards {
                var swiftMap: [InstanceType:Bool] = [:]
                for (key, value) in map {
                    if let instanceType = key as? InstanceType {
                        swiftMap[instanceType] = value.boolValue
                    }
                }
                self.showInfoCardMap = swiftMap
            }
        }
        Task {
            for await map in preferenceStore.viewType {
                var swiftMap: [InstanceType:ViewType] = [:]
                for (key, value) in map {
                    if let instanceType = key as? InstanceType {
                        swiftMap[instanceType] = value
                    }
                }
                self.viewTypeMap = swiftMap
            }
        }
    }
    
    func saveSortBy(_ value: SortBy) {
        preferenceStore.saveSortBy(sortBy: value)
    }
    
    func saveSortOrder(_ value: Shared.SortOrder) {
        preferenceStore.saveSortOrder(sortOrder: value)
    }
    
    func saveFilterBy(_ value: FilterBy) {
        preferenceStore.saveFilterBy(filterBy: value)
    }
    
    func setInfoCardVisibility(type: InstanceType, visible: Bool) {
        preferenceStore.setInfoCardVisibility(type: type, value: visible)
    }
    
    func saveViewType(type: InstanceType, viewType: ViewType) {
        preferenceStore.saveViewType(instanceType: type, viewType: viewType)
    }
    
}
