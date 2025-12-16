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
    
    @EnvironmentObject var navigation: NavigationManager
    
    @ObservedObject var networkViewModel: NetworkConnectivityViewModel = NetworkConnectivityViewModel()
    @ObservedObject var instanceViewModel: InstanceViewModel = InstanceViewModel()
    @ObservedObject var preferences: PreferencesViewModel = PreferencesViewModel()
    
    @State private var arrViewModel: ArrViewModel? = nil
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @State private var stableItemsKey: String = UUID().uuidString
    
    private var viewType: ViewType {
        preferences.viewTypeMap[type] ?? .grid
    }
    
    private var firstInstance: Instance? {
        instanceViewModel.firstInstance
    }
    
    init(type: InstanceType) {
        self.type = type
    }
    
    var body: some View {
        contentForState()
            .navigationTitle(firstInstance?.label ?? firstInstance?.type.name ?? "")
            .task {
                await setupViewModel()
            }
            .onDisappear {
                observationTask?.cancel()
            }
            .toolbar {
                if !networkViewModel.isConnected {
                    ToolbarItem(placement: .navigation) {
                        Image(systemName: "wifi.slash")
                            .imageScale(.medium)
                            .foregroundColor(.red)
                            .onTapGesture {
                                let errTitle = String(localized: LocalizedStringResource("no_network"))
                                let toast = Toast.text(errTitle)
                                toast.show()
                            }
                    }
                }
                
                if let error = uiState as? LibraryUiStateError<AnyObject> {
                    if error.type == .network {
                        ToolbarItem(placement: .navigation) {
                            Image(systemName: "externaldrive.badge.xmark")
                                .imageScale(.medium)
                                .foregroundColor(.red)
                                .onTapGesture {
                                    let errTitle = String(localized: LocalizedStringResource("instance_connect_error_ios"))
                                    let errSubtitle = "\(firstInstance?.label ?? firstInstance?.type.name ?? "") - \(firstInstance?.url ?? "")"
                                    let toast = Toast.text(errTitle, subtitle: errSubtitle)
                                    toast.show()
                                }
                        }
                    }
                }
                
                if uiState is LibraryUiStateSuccess<AnyObject> {
                    toolbarOptions
                }
            }
            .refreshable {
                await arrViewModel?.refreshLibrary()
            }
    }
    
    private var sortedAndFilteredItems: [AnyArrMedia] {
        guard case let success = uiState as? LibraryUiStateSuccess<AnyObject>,
              let items = success?.items as? [AnyArrMedia] else { return [] }
        
        let sorted = SortByKt.applySorting(items, type: type, sortBy: preferences.sortBy, order: preferences.sortOrder) as [AnyArrMedia]
        let filtered = FilterByKt.applyFiltering(sorted, type: type, filterBy: preferences.filterBy) as [AnyArrMedia]
        
        return filtered
    }
    
    private var itemIdentifiers: [Int32] {
        sortedAndFilteredItems.map(\.id)
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
        case _ as LibraryUiStateSuccess<AnyObject>:
            if sortedAndFilteredItems.isEmpty {
                Text("No items")
            } else {
                mediaView(items: sortedAndFilteredItems) { media in
                    navigation.go(to: .details(Int(media.id)), of: type)
                }
                .id(stableItemsKey)
                .onChange(of: itemIdentifiers) { _, _ in
                    stableItemsKey = UUID().uuidString
                }
                .ignoresSafeArea(edges: .bottom)
            }
        case let error as LibraryUiStateError<AnyObject>:
            ZStack {
                VStack {
                    Text("An error occurred")
                        .font(.headline)
                    Text(error.error.message)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
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
        
        guard let firstInstance = self.firstInstance else { return }
        
        self.arrViewModel = createArrViewModel(for: firstInstance)
        
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
        let newType = switch viewType {
        case .grid: ViewType.list
        case .list: ViewType.grid
        }
        let image = switch newType {
        case .grid: "rectangle.grid.2x2"
        case .list: "rectangle.grid.1x2"
        }
        ToolbarItem(placement: .primaryAction) {
            Image(systemName: image)
                .imageScale(.medium)
                .onTapGesture {
                    preferences.saveViewType(type: type, viewType: newType)
                }
        }
        
        ToolbarItem(placement: .primaryAction) {
            SortByPickerMenu(type: type, sortedBy: $preferences.sortBy, sortOrder: $preferences.sortOrder)
                .onChange(of: preferences.sortBy) { _, newValue in
                    preferences.saveSortBy(newValue)
                }
                .onChange(of: preferences.sortOrder) { _, newValue in
                    preferences.saveSortOrder(newValue)
                }
                .menuIndicator(.hidden)
        }
        
        ToolbarItem(placement: .primaryAction) {
            FilterByPickerMenu(type: type, filteredBy: $preferences.filterBy)
                .onChange(of: preferences.filterBy) { _, newValue in
                    preferences.saveFilterBy(newValue)
                }
                .menuIndicator(.hidden)
        }
    }
     @ViewBuilder
    private func mediaView(
        items: [AnyArrMedia],
        onItemClicked: @escaping (AnyArrMedia) -> Void
    ) -> some View {
        switch viewType {
        case .grid: PosterGridView(items: items, onItemClick: onItemClicked)
        case .list: PosterListView(items: items, onItemClick: onItemClicked)
        }
    }
    
}
