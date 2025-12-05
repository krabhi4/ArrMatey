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

}
