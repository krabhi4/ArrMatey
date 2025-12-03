//
//  InstanceViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

class InstanceViewModel: ObservableObject {
    let instanceType: InstanceType
    
    init(instanceType: InstanceType) {
        self.instanceType = instanceType
    }
    
    private let instanceRepository = InstanceRepository()
    
    @Published
    private(set) var instances: [Instance] = []
    
    @Published
    private(set) var firstInstance: Instance? = nil
    
    @MainActor
    func refresh() async {
        for await instances in instanceRepository.allInstances {
            self.instances = instances
        }
    }
    
    @MainActor
    func getFirstInstance() async {
        if let instance = await instanceRepository.getFirstInstance(instanceType: instanceType).first(where: { _ in true }) {
            self.firstInstance = instance
        }
    }
}
