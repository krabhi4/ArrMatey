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
    
    @EnvironmentObject private var navigation: NavigationManager
    @EnvironmentObject private var arrTabViewModel: ArrTabViewModel
    
    @ObservedObject private var networkViewModel: NetworkConnectivityViewModel = NetworkConnectivityViewModel()
    @ObservedObject private var preferences: PreferencesViewModel = PreferencesViewModel()
    
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @State private var searchQuery: String = ""
    @State private var searchPresented: Bool = false
    
    @State private var stableItemsKey: String = UUID().uuidString
    
    private var instance: Instance? {
        arrTabViewModel.currentInstance
    }
    
    private var arrViewModel: ArrViewModel? {
        arrTabViewModel.arrViewModel
    }
    
    private var viewType: ViewType {
        preferences.viewTypeMap[type] ?? .grid
    }
    
    init(type: InstanceType) {
        self.type = type
    }
    
    var body: some View {
        contentForState()
            .navigationTitle(instance?.label ?? "")
            .task {
                await setupViewModel()
            }
            .onChange(of: arrTabViewModel.currentInstance?.id) { _, _ in
                Task {
                    await setupViewModel()
                }
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
                                    let errSubtitle = "\(instance?.label ?? instance?.type.name ?? "") - \(instance?.url ?? "")"
                                    let toast = Toast.text(errTitle, subtitle: errSubtitle)
                                    toast.show()
                                }
                        }
                    }
                }
                
                if uiState is LibraryUiStateSuccess<AnyObject> {
                    toolbarOptions
                }
                
                ToolbarItem(placement: .navigation) {
                    InstancePickerMenu(type: type)
                        .menuIndicator(.hidden)
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
        
        if searchQuery.isEmpty { return filtered }
        return filtered.filter { $0.title.localizedCaseInsensitiveContains(searchQuery) }
    }
    
    private var itemIdentifiers: [Int32] {
        sortedAndFilteredItems.compactMap { $0.id as? Int32 }
    }
    
    @ViewBuilder
    private func contentForState() -> some View {
        if instance == nil {
            VStack {
                noInstanceView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            switch uiState {
            case is LibraryUiStateInitial:
                VStack {
                    noInstanceView()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            case is LibraryUiStateLoading:
                ZStack {
                    ProgressView()
                        .progressViewStyle(.circular)
                }
            case _ as LibraryUiStateSuccess<AnyObject>:
                if sortedAndFilteredItems.isEmpty {
                    VStack {
                        emptyLibraryView()
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    mediaView(items: sortedAndFilteredItems) { media in
                        if let id = media.id as? Int {
                            navigation.go(to: .details(id), of: type)
                        }
                    }
                    .id(stableItemsKey)
                    .onChange(of: itemIdentifiers) { _, _ in
                        stableItemsKey = UUID().uuidString
                    }
                    .ignoresSafeArea(edges: .bottom)
                    .searchable(text: $searchQuery, isPresented: $searchPresented, placement: .navigationBarDrawer)
                }
            case _ as LibraryUiStateError<AnyObject>:
                ZStack {
                    errorView()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            default:
                VStack {
                    noInstanceView()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
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
            let flow = viewModel.getUiState()
            for try await state in flow {
                self.uiState = state
            }
        }
    }
    
    @ToolbarContentBuilder
    var toolbarOptions: some ToolbarContent {
        ToolbarItem(placement: .navigation) {
            SortByPickerMenu(type: type, sortedBy: $preferences.sortBy, sortOrder: $preferences.sortOrder)
                .onChange(of: preferences.sortBy) { _, newValue in
                    preferences.saveSortBy(newValue)
                }
                .onChange(of: preferences.sortOrder) { _, newValue in
                    preferences.saveSortOrder(newValue)
                }
                .menuIndicator(.hidden)
        }
        
        ToolbarItem(placement: .navigation) {
            FilterByPickerMenu(type: type, filteredBy: $preferences.filterBy)
                .onChange(of: preferences.filterBy) { _, newValue in
                    preferences.saveFilterBy(newValue)
                }
                .menuIndicator(.hidden)
        }
        
        if #available(iOS 26.0, *) {
            ToolbarSpacer(.flexible, placement: .navigation)
        }
        
        
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
        
        if #available(iOS 26.0, *) {
            ToolbarSpacer(.flexible, placement: .primaryAction)
        }
        
        ToolbarItem(placement: .primaryAction) {
            Image(systemName: "plus")
                .imageScale(.medium)
                .onTapGesture {
                    navigation.go(to: .search(""), of: type)
                }
        }
        
        ToolbarItem(placement: .bottomBar) {
            Image(systemName: "magnifyingglass")
                .imageScale(.medium)
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
    
    @ViewBuilder
    private func errorView() -> some View {
        VStack(alignment: .center, spacing: 8) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 64))
                .imageScale(.large)
            
            Text("couldnt_connect")
                .font(.system(size: 20, weight: .medium))
                .multilineTextAlignment(.center)
            Text("couldnt_connect_message")
                .multilineTextAlignment(.center)
            Button(action: {
                Task {
                    await arrViewModel?.refreshLibrary()
                }
            }) {
                Text("retry")
            }
        }
        .padding(.horizontal, 24)
    }
    
    @ViewBuilder
    private func noInstanceView() -> some View {
        VStack(alignment: .center, spacing: 8) {
            Image(systemName: "externaldrive.fill.trianglebadge.exclamationmark")
                .font(.system(size: 64))
                .imageScale(.large)
            
            Text("no_type_instances \(type)")
                .font(.system(size: 20, weight: .medium))
                .multilineTextAlignment(.center)
            Text(String(localized: LocalizedStringResource("no_type_instances_message \(type)")))
                .multilineTextAlignment(.center)
        }
        .padding(.horizontal, 24)
    }
    
    @ViewBuilder
    private func emptyLibraryView() -> some View {
        VStack(alignment: .center, spacing: 8) {
            Image(systemName: "popcorn.fill")
                .font(.system(size: 64))
                .imageScale(.large)
            
            Text("empty_library")
                .font(.system(size: 20, weight: .medium))
                .multilineTextAlignment(.center)
            Text("empty_library_message")
                .multilineTextAlignment(.center)
        }
        .padding(.horizontal, 24)
    }
    
}
