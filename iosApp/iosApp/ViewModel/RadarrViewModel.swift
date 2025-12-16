//
//  RadarrViewModel.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import Shared

class RadarrViewModel: ArrViewModel {
    var repository: any IArrRepository
    
    let instance: Instance
    
    private var radarrRespository: RadarrRepository {
        repository as! RadarrRepository
    }
    
    init(instance: Instance) {
        self.instance = instance
        self.repository = RadarrRepository(instance: instance)
    }
}
