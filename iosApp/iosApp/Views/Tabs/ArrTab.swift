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
    private let type: InstanceType
    
    @ObservedObject private var arrMediaViewModel: ArrMediaViewModelS
    @ObservedObject private var instancesViewModel: InstancesViewModelS
    @ObservedObject private var activityQueueViewModel: ActivityQueueViewModelS = ActivityQueueViewModelS()
    @ObservedObject private var networkViewModel: NetworkConnectivityViewModel = NetworkConnectivityViewModel()
    
    @EnvironmentObject private var navigation: NavigationManager
    
    @State private var searchPresented: Bool = false
    
    private var uiState: ArrLibrary {
        arrMediaViewModel.uiState
    }
    
    private var instanceState: InstancesState {
        instancesViewModel.instancesState
    }
    
    private var queueItems: [QueueItem] {
        activityQueueViewModel.queueItems
    }
    
    private var preferences: InstancePreferences {
        arrMediaViewModel.preferences
    }
    
    
    init(type: InstanceType) {
        self.type = type
        self.arrMediaViewModel = ArrMediaViewModelS(type: type)
        self.instancesViewModel = InstancesViewModelS(type: type)
    }
    
    var body: some View {
        contentForState
            .navigationTitle(instanceState.selectedInstance?.label ?? type.name)
            .toolbar {
                toolbarContent
            }
            .refreshable {
                arrMediaViewModel.refresh()
            }
            .onReceive(instancesViewModel.$instancesState) { _ in
                if instanceState.selectedInstance != nil {
                    arrMediaViewModel.refresh()
                }
            }
            .task {
                if instanceState.selectedInstance != nil {
                    arrMediaViewModel.refresh()
                }
            }
    }
    
    @ViewBuilder
    private var contentForState: some View {
        if instanceState.selectedInstance == nil {
            VStack {
                noInstanceView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if uiState is ArrLibraryInitial {
            VStack {
                noInstanceView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if uiState is ArrLibraryLoading {
            ZStack {
                ProgressView()
                    .progressViewStyle(.circular)
            }
        } else if let success = uiState as? ArrLibrarySuccess
        {
            ArrLibraryView(type: type, state: success, searchQuery: $arrMediaViewModel.searchQuery, searchPresented: $searchPresented)
        } else if uiState is ArrLibraryError {
            ZStack {
                errorView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            VStack {
                noInstanceView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        if !networkViewModel.isConnected {
            ToolbarItem(placement: .navigation) {
                Image(systemName: "wifi.slash")
                    .imageScale(.medium)
                    .foregroundColor(.red)
                    .onTapGesture {
//                        let errTitle = String(localized: LocalizedStringResource("no_network"))
//                        let toast = Toast.text(errTitle)
//                        toast.show()
                    }
            }
        }
        
        if let error = uiState as? ArrLibraryError {
            if error.type == .network {
                ToolbarItem(placement: .navigation) {
                    Image(systemName: "externaldrive.badge.xmark")
                        .imageScale(.medium)
                        .foregroundColor(.red)
                        .onTapGesture {
//                            let errTitle = String(localized: LocalizedStringResource("instance_connect_error_ios"))
//                            let errSubtitle = "\(instanceState.selectedInstance?.label ?? instanceState.selectedInstance?.type.name ?? "") - \(instanceState.selectedInstance?.url ?? "")"
//                            let toast = Toast.text(errTitle, subtitle: errSubtitle)
//                            toast.show()
                        }
                }
            }
        }
        
        if uiState is ArrLibrarySuccess {
            toolbarViewOptions
        }
        
        ToolbarItem(placement: .navigation) {
            InstancePickerMenu(
                instances: instanceState.instances,
                onChangeInstance: { instancesViewModel.setInstanceActive($0) }
            )
            .menuIndicator(.hidden)
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarViewOptions: some ToolbarContent {
        ToolbarItem(placement: .primaryAction) {
            SortByPickerMenu(
                type: type,
                sortBy: preferences.sortBy,
                sortOrder: preferences.sortOrder,
                changeSortBy: { newValue in
                    arrMediaViewModel.updateSortBy(newValue)
                },
                changeSortOrder: { newValue in
                    arrMediaViewModel.updateSortOrder(newValue)
                }
            )
            .menuIndicator(.hidden)
        }
        
        ToolbarItem(placement: .primaryAction) {
            FilterByPickerMenu(
                type: type,
                filterBy: preferences.filterBy,
                changeFilterBy: { newValue in
                    arrMediaViewModel.updateFilterBy(newValue)
                })
                .menuIndicator(.hidden)
        }
        
        if #available(iOS 26.0, *) {
            ToolbarSpacer(.flexible, placement: .navigation)
        }
        
        
        let newType = switch preferences.viewType {
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
                    arrMediaViewModel.updateViewType(newType)
                }
        }
        
        if #available(iOS 26.0, *) {
            ToolbarSpacer(.flexible, placement: .primaryAction)
        }
        
        ToolbarItem(placement: .navigation) {
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
                arrMediaViewModel.refresh()
            }) {
                Text("retry")
            }
        }
        .padding(.horizontal, 24)
    }
    
    @ViewBuilder
    private func noInstanceView() -> some View {
        VStack(alignment: .center, spacing: 12) {
            Image(systemName: "externaldrive.fill.trianglebadge.exclamationmark")
                .font(.system(size: 64))
                .imageScale(.large)
            
            VStack(spacing: 4) {
                Text(LocalizedStringResource("no_type_instances \(type.name)"))
                    .font(.system(size: 20, weight: .bold))
                    .multilineTextAlignment(.center)
            
                Text(LocalizedStringResource("no_type_instances_message \(type.name)"))
                    .multilineTextAlignment(.center)
            }
            
            Button(action: {
                navigation.selectedTab = .settings
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    navigation.go(to: .newInstance(type))
                }
            }) {
                HStack {
                    Image(systemName: "plus.circle.fill")
                        .foregroundColor(.primary)
                    Text(LocalizedStringResource("add_instance"))
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.primary)
                }
                .padding(.horizontal, 32)
                .padding(.vertical, 12)
                .background(.primary.opacity(0.1))
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
        .padding(.horizontal, 24)
    }
    
}
