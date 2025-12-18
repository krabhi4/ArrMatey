//
//  InstanceViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

@MainActor
class InstanceViewModel: ObservableObject {
    private let instanceRepository = InstanceRepository()
    
    @Published
    private(set) var instances: [Instance] = []
    
    init() {
        Task {
            for await value in instanceRepository.allInstances {
                self.instances = value
            }
        }
    }
    
    func setSelected(_ instance: Instance) {
        Task {
            do {
                try await instanceRepository.setInstanceActive(instance: instance)
            } catch {
                return
            }
        }
    }
    
    func delete(_ instance: Instance) {
        Task {
            do {
                try await instanceRepository.deleteInstance(instance: instance)
            } catch {
                return
            }
        }
    }
    
}
