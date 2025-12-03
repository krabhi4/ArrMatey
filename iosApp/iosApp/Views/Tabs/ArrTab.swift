//
//  ArrTab.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import Foundation
import SwiftUI
import Shared

struct ArrTab: View {
    let type: InstanceType
    
    @ObservedObject var instanceViewModel: InstanceViewModel
    @State private var arrViewModel: ArrViewModel? = nil
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    init(type: InstanceType) {
        self.type = type
        self.instanceViewModel = InstanceViewModel(instanceType: type)
    }
    
    var body: some View {
        contentForState()
            .navigationTitle(instanceViewModel.firstInstance?.label ?? "")
            .task {
                await setupViewModel()
            }
            .onDisappear {
                observationTask?.cancel()
            }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        switch uiState {
        case is LibraryUiStateInitial:
            ZStack {
                Text("initial state")
            }
        case is LibraryUiStateLoading:
            ZStack {
                Text("loading")
            }
        case let success as LibraryUiStateSuccess<AnyObject>:
            if let items = success.items as? [GenericArrMedia] {
                PosterGridView(items: items) { media in
                    print("tapped: \(media.title)")
                }
                .ignoresSafeArea(edges: .bottom)
            } else {
                Text("unable to cast items")
            }
        case let error as LibraryUiStateError<AnyObject>:
            VStack {
                Text("error: \(error)")
            }
        default:
            VStack {
                Text("default")
            }
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        await instanceViewModel.getFirstInstance()
        
        guard let firstInstance = instanceViewModel.firstInstance else { return }
        
        self.arrViewModel = ArrViewModel(instance: firstInstance)
        
        observationTask?.cancel()
        observationTask = Task {
            await observeUiState()
        }
    }
    
    @MainActor
    private func observeUiState() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.getUiState()
            for try await state in flow {
                self.uiState = state
            }
        } catch {
            print("Error observing state: \(error)")
        }
    }
}
