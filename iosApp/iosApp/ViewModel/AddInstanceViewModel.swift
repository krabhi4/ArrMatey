//
//  AddInstanceViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-04.
//

import SwiftUI
import Shared

@MainActor
class AddInstanceViewModel: ObservableObject {
    private let repository = AddInstanceRepository()
        
    @Published var apiEndpoint: String = ""
    @Published var apiKey: String = ""
    @Published var saveButtonEnabled: Bool = false
    @Published var infoCardMap: [InstanceType:KotlinBoolean] = [:]
    @Published var endpointError: Bool = false
    @Published var testing: Bool = false
    @Published var result: Bool? = nil
    @Published var isSlowInstance: Bool = false
    @Published var customTimeout: Int64? = nil
    @Published var instanceLabel: String = ""
    @Published var createResult: InsertResult? = nil
    @Published var editResult: InsertResult? = nil
    @Published var wasCreatedSuccessfully: Bool = false
    @Published var hasCreationError: Bool = false
    
    init() {
        observeFlows()
    }
    
    private func observeFlows() {
        Task {
            for await value in repository.apiEndpoint {
                apiEndpoint = value
            }
        }
        
        Task {
            for await value in repository.apiKey {
                apiKey = value
            }
        }
        
        Task {
            for await value in repository.saveButtonEnabled {
                saveButtonEnabled = value.boolValue
            }
        }
        
        Task {
            for await value in repository.infoCardMap {
                infoCardMap = value
            }
        }
        
        Task {
            for await value in repository.endpointError {
                endpointError = value.boolValue
            }
        }
        
        Task {
            for await value in repository.testing {
                testing = value.boolValue
            }
        }
        
        Task {
            for await value in repository.result {
                result = value?.boolValue
            }
        }
        
        Task {
            for await value in repository.isSlowInstance {
                isSlowInstance = value.boolValue
            }
        }
        
        Task {
            for await value in repository.customTimeout {
                customTimeout = value?.int64Value
            }
        }
        
        Task {
            for await value in repository.instanceLabel {
                instanceLabel = value
            }
        }
        Task {
            for await value in repository.createResult {
                createResult = value
                wasCreatedSuccessfully = createResult is InsertResultSuccess
                hasCreationError = createResult is InsertResultError || createResult is InsertResultConflict
            }
        }
        Task {
            for await value in repository.editResult {
                print("edit result: \(value)")
                editResult = value
                wasCreatedSuccessfully = editResult is InsertResultSuccess
                hasCreationError = editResult is InsertResultError || editResult is InsertResultConflict
            }
        }
    }
    
    func setApiEndpoint(_ value: String) {
        repository.setApiEndpoint(value: value)
    }
    
    func setApiKey(_ value: String) {
        repository.setApiKey(value: value)
    }
    
    func setIsSlowInstance(_ value: Bool) {
        repository.setIsSlowInstance(value: value)
    }
    
    func setCustomTimeout(_ value: Int64?) {
        repository.setCustomTimeout(value: value?.kotlinLong)
    }
    
    func setInstanceLabel(_ value: String) {
        repository.setInstanceLabel(value: value)
    }
    
    func testConnection() {
        Task {
            try await repository.testConnection()
        }
    }
    
    func reset() {
        repository.reset()
    }
    
    func dismissInfoCard(instanceType: InstanceType) {
        repository.dismissInfoCard(instanceType: instanceType)
    }
    
    func saveInstance(instanceType: InstanceType) {
        Task {
            try await repository.createInstance(instanceType: instanceType)
        }
    }
    
    func updateInstance(instance: Instance) {
        Task {
            try await repository.updateInstance(instance: instance)
        }
    }
    
    func initialize(instance: Instance) {
        print("initializing with instance \(instance)")
        setApiEndpoint(instance.url)
        setApiKey(instance.apiKey)
        setIsSlowInstance(instance.slowInstance)
        setCustomTimeout(instance.customTimeout?.int64Value)
        setInstanceLabel(instance.label)
    }
    
    var customTimeoutTextBinding: Binding<String> {
        Binding(
            get: {
                guard let value = self.customTimeout else { return "" }
                return String(value)
            },
            set: { newValue in
                let trimmed = newValue.trimmingCharacters(in: .whitespacesAndNewlines)
                if trimmed.isEmpty {
                    self.setCustomTimeout(nil)
                } else if let intValue = Int64(trimmed) {
                    self.setCustomTimeout(intValue)
                } else {
                    // do nothing
                }
            }
        )
    }
}

// Helper extension for KotlinLong conversion
extension Int64 {
    var kotlinLong: KotlinLong {
        KotlinLong(value: self)
    }
}
