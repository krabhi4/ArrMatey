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
    
    private var observationTask: Task<Void, Never>?
    
    init() {
        observationTask = Task {
            do {
                for try await value in instanceRepository.allInstances {
                    self.instances = value
                }
            } catch {
                print("Error observing instance: \(error)")
            }
        }
    }

    deinit {
        observationTask?.cancel()
    }
    
    func selectedInstance(for type: InstanceType) -> Instance? {
        instances.first { $0.selected && $0.type == type }
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
