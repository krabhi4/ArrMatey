//
//  ArrViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

class ArrViewModel {
    let instance: Instance
    private let repository: IArrRepository
    
    func getUiState() -> SkieSwiftStateFlow<LibraryUiState> {
        return repository.uiState
    }
    
    func getDetailsUiState() -> SkieSwiftStateFlow<DetailsUiState> {
        return repository.detailUiState
    }
    
    init(instance: Instance) {
        self.instance = instance
        self.repository = BaseArrRepositoryKt.createInstanceRepository(instance: instance)
    }
    
    func refreshLibrary() async {
        do {
            try await repository.refreshLibrary()
        } catch {
            return
        }
    }
    
    func getDetails(id: Int32) async {
        do {
            try await repository.getDetails(id: id)
        } catch {
            return
        }
    }
    
    func setMonitorStatus(id: Int32, monitorStatus: Bool) async {
        do {
            try await repository.setMonitorStatus(id: id, monitorStatus: monitorStatus)
        } catch {
            return
        }
    }

}
