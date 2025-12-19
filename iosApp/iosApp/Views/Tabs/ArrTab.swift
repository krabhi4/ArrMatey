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
    @EnvironmentObject private var instanceViewModel: InstanceViewModel
    
    @ObservedObject var networkViewModel: NetworkConnectivityViewModel = NetworkConnectivityViewModel()
    @ObservedObject var preferences: PreferencesViewModel = PreferencesViewModel()
    
    @State private var arrViewModel: ArrViewModel? = nil
    @State private var uiState: Any = LibraryUiStateInitial()
    @State private var observationTask: Task<Void, Never>? = nil
    
    @State private var stableItemsKey: String = UUID().uuidString
    
    private var viewType: ViewType {
        preferences.viewTypeMap[type] ?? .grid
    }
    
    private var instance: Instance? {
        instanceViewModel.instances.first {
            $0.type == type && $0.selected
        }
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
            .onChange(of: instance) { _, _ in
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
                        navigation.go(to: .details(Int(media.id)), of: type)
                    }
                    .id(stableItemsKey)
                    .onChange(of: itemIdentifiers) { _, _ in
                        stableItemsKey = UUID().uuidString
                    }
                    .ignoresSafeArea(edges: .bottom)
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
        arrViewModel = nil
        
        guard let instance = self.instance else { return }
        self.arrViewModel = createArrViewModel(for: instance)
        
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
        ToolbarItem(placement: .navigation) {
            Image(systemName: image)
                .imageScale(.medium)
                .onTapGesture {
                    preferences.saveViewType(type: type, viewType: newType)
                }
        }
        
        if #available(iOS 26.0, *) {
            ToolbarSpacer(.flexible, placement: .navigation)
        }
        
        ToolbarItem(placement: .navigation) {
            InstancePickerMenu(type: type)
                .menuIndicator(.hidden)
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
