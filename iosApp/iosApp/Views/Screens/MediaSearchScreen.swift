//
//  MediaSearchScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-27.
//

import SwiftUI
import Shared

struct MediaSearchScreen: View {
    private let type: InstanceType
    
    @Environment(\.dismiss) var dismiss
    
    @EnvironmentObject private var navigation: NavigationManager
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel
    
    @State private var searchQuery: String
    @State private var searchPresented: Bool = false
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    init(query: String, type: InstanceType) {
        self._searchQuery = .init(initialValue: query)
        self.type = type
    }
    
    private var instance: Instance? {
        arrTabViewModel.currentInstance
    }
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    private var sortedItems: [AnyArrMedia] {
        guard case let success = uiState as? LibraryUiStateSuccess<AnyObject>, let items = success?.items as? [AnyArrMedia] else { return [] }
        
        // todo - add sorting
        return items
    }
    
    var body: some View {
        contentForState()
            .task {
                await setupViewModel()
            }
            .task {
                try? await Task.sleep(nanoseconds: 500_000_000)
                searchPresented = true
            }
            .onDebounceSearch(searchQuery) { query in
                guard !query.isEmpty else { return }
                await arrViewModel?.performLookup(query)
            }
            .onDisappear {
                observationTask?.cancel()
            }
            .toolbar {
                toolbarContent
            }
            .searchable(text: $searchQuery, isPresented: $searchPresented, placement: .navigationBarDrawer)
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        if instance == nil {
            EmptyView()
        } else {
            switch uiState {
            case is LibraryUiStateInitial:
                Color.clear
            case is LibraryUiStateLoading:
                ZStack {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            case _ as LibraryUiStateSuccess<AnyObject>:
                PosterGridView(items: sortedItems, onItemClick: { media in
                    if let id = media.id as? Int {
                        navigation.go(to: .details(id), of: type)
                    } else {
                        let json = media.toJson()
                        navigation.go(to: .preview(json), of: type)
                    }
                    
                })
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .ignoresSafeArea(edges: .bottom)
            case _ as LibraryUiStateError<AnyObject>:
                Text("error state")
            default: EmptyView()
            }
        }
    }
    
    @MainActor
    private func setupViewModel() async {
        observationTask?.cancel()
        observationTask = Task {
            await observeUiState()
        }
    }
    
    @MainActor
    private func observeUiState() async {
        guard let viewModel = arrViewModel else { return }
        
        do {
            let flow = viewModel.getLookupUiState()
            for try await state in flow {
                self.uiState = state
            }
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .bottomBar) {
            Image(systemName: "magnifyingglass")
                .imageScale(.medium)
        }
    }
}
