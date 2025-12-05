//
//  ArrTab.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import Foundation
import SwiftUI
import Shared
import ToastViewSwift

struct ArrTab: View {
    let type: InstanceType
    
    @ObservedObject var instanceViewModel: InstanceViewModel = InstanceViewModel()
    @State private var arrViewModel: ArrViewModel? = nil
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @State private var sortBy: Shared.SortBy = .title
    @State private var sortOrder: Shared.SortOrder = .asc
    @State private var filterBy: Shared.FilterBy = .all
    
    @State private var stableItemsKey: String = UUID().uuidString
    
    init(type: InstanceType) {
        self.type = type
    }
    
    var body: some View {
        contentForState()
            .navigationTitle(instanceViewModel.firstInstance?.label ?? instanceViewModel.firstInstance?.type.name ?? "")
            .task {
                await setupViewModel()
            }
            .onDisappear {
                observationTask?.cancel()
            }
            .toolbar {
                if uiState is LibraryUiStateSuccess<AnyObject> {
                    toolbarOptions
                }
            }
            .refreshable {
                await arrViewModel?.refreshLibrary()
            }
    }
    
    private var sortedAndFilteredItems: [GenericArrMedia] {
        guard case let success = uiState as? LibraryUiStateSuccess<AnyObject>,
              let items = success?.items as? [GenericArrMedia] else { return [] }
        
        guard let sorted = SortByKt.applySorting(items, type: type, sortBy: sortBy, order: sortOrder) as? [GenericArrMedia] else { return [] }
        
        guard let filtered = FilterByKt.applyFiltering(sorted, type: type, filterBy: filterBy) as? [GenericArrMedia] else { return [] }
        
        return filtered
    }
    
    private var sortedAndFilteredCacheItems: [GenericArrMedia] {
        guard case let error = uiState as? LibraryUiStateError<AnyObject>,
              let items = error?.cachedItems as? [GenericArrMedia] else { return [] }
        
        guard let sorted = SortByKt.applySorting(items, type: type, sortBy: sortBy, order: sortOrder) as? [GenericArrMedia] else { return [] }
        
        guard let filtered = FilterByKt.applyFiltering(sorted, type: type, filterBy: filterBy) as? [GenericArrMedia] else { return [] }
        
        return filtered
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
                ProgressView()
                    .progressViewStyle(.circular)
            }
        case let success as LibraryUiStateSuccess<AnyObject>:
            if sortedAndFilteredItems.isEmpty {
                Text("No items")
            } else {
                PosterGridView(items: sortedAndFilteredItems) { media in
                    print("tapped: \(media.title)")
                }
                .id(stableItemsKey)
                .onChange(of: sortedAndFilteredItems) { _, _ in
                    stableItemsKey = UUID().uuidString
                }
                .ignoresSafeArea(edges: .bottom)
            }
        case let error as LibraryUiStateError<AnyObject>:
            ZStack {
                // Show cached items if available, otherwise show error message
                if !error.cachedItems.isEmpty {
                    PosterGridView(items: sortedAndFilteredCacheItems) { media in
                        print("tapped: \(media.title)")
                    }
                    .ignoresSafeArea(edges: .bottom)
                } else {
                    VStack {
                        Text("An error occurred")
                            .font(.headline)
                        Text(error.error.message)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
            }
            .onAppear {
                print("GOT ERROR \(error.error.message)")
                let toast = Toast.text(error.error.message)
                toast.show()
            }
        default:
            VStack {
                Text("default")
            }
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        await instanceViewModel.getFirstInstance(instanceType: type)
        
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
    
    @ToolbarContentBuilder
    var toolbarOptions: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            SortByPickerMenu(type: type, sortedBy: self.$sortBy, sortOrder: self.$sortOrder)
                .menuIndicator(.hidden)
        }
        
        ToolbarItem(placement: .primaryAction) {
            FilterByPickerMenu(type: type, filteredBy: self.$filterBy)
                .menuIndicator(.hidden)
        }
    }
}
