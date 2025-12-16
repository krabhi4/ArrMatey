//
//  ArrViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

protocol ArrViewModel {
    func getUiState() -> SkieSwiftStateFlow<LibraryUiState>
    func getDetailsUiState() -> SkieSwiftStateFlow<DetailsUiState>
    func refreshLibrary() async
    func getDetails(id: Int32) async
    func setMonitorStatus(id: Int32, isMonitored: Bool) async
    
    var instance: Instance { get }
    var repository: IArrRepository { get }
}

extension ArrViewModel {
    func getUiState() -> SkieSwiftStateFlow<LibraryUiState> {
        return repository.uiState
    }
    
    func getDetailsUiState() -> SkieSwiftStateFlow<DetailsUiState> {
        return repository.detailUiState
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
    
    func setMonitorStatus(id: Int32, isMonitored: Bool) async {
        do {
            try await repository.setMonitorStatus(id: id, monitorStatus: isMonitored)
        } catch {
            return
        }
    }
}

func createArrViewModel(for instance: Instance) -> any ArrViewModel {
    switch instance.type {
    case .sonarr: return SonarrViewModel(instance: instance)
    case .radarr: return RadarrViewModel(instance: instance)
    }
}
