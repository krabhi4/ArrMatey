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
    func getLookupUiState() -> SkieSwiftStateFlow<LibraryUiState>
    func getAddItemUiState() -> SkieSwiftStateFlow<DetailsUiState>
    
    func rootFolders() -> SkieSwiftStateFlow<[RootFolder]>
    func tags() -> SkieSwiftStateFlow<[Tag]>
    func qualityProfiles() -> SkieSwiftStateFlow<[QualityProfile]>
    
    func refreshLibrary() async
    func getDetails(id: Int32) async
    func setMonitorStatus(id: Int32, isMonitored: Bool) async
    func performLookup(_ query: String) async
    func addItem(item: AnyArrMedia) async
    
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
    
    func getLookupUiState() -> SkieSwiftStateFlow<LibraryUiState> {
        return repository.lookupUiState
    }
    
    func getAddItemUiState() -> SkieSwiftStateFlow<DetailsUiState> {
        return repository.addItemUiState
    }
    
    func rootFolders() -> SkieSwiftStateFlow<[RootFolder]> {
        return repository.rootFolders
    }
    
    func tags() -> SkieSwiftStateFlow<[Tag]> {
        return repository.tags
    }
    
    func qualityProfiles() -> SkieSwiftStateFlow<[QualityProfile]> {
        return repository.qualityProfiles
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
    
    func performLookup(_ query: String) async {
        do {
            try await repository.lookup(query: query)
        } catch {
            return
        }
    }
    
    func addItem(item: AnyArrMedia) async {
        do {
            try await repository.addItem(item: item)
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
