//
//  ArrLibraryView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-20.
//

import SwiftUI
import Shared

struct ArrLibraryView: View {
    let type: InstanceType
    let state: ArrLibrarySuccess
    @Binding var searchQuery: String
    @Binding var searchPresented: Bool
    
    @EnvironmentObject private var navigation: NavigationManager
    
    @ObservedObject private var activityQueueViewModel = ActivityQueueViewModelS()
    
    private var queueItems: [QueueItem] {
        activityQueueViewModel.queueItems
    }
    
    var body: some View {
        if state.items.isEmpty {
            VStack {
                EmptyLibraryView()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            contentView(items: state.items, prefs: state.preferences)
        }
    }
    
    private func contentView(
        items: [ArrMedia],
        prefs: InstancePreferences
    ) -> some View {
        let base: AnyView = AnyView(
            mediaView(
                viewType: prefs.viewType,
                items: items,
                onItemClicked: { media in
                    if let id = media.id as? Int64 {
                        navigation.go(to: .details(id), of: type)
                    }
                },
                itemIsActive: { item in
                    queueItems.contains(where: { $0.mediaId == item.id })
                }
            )
            .id(items.count)
            .ignoresSafeArea(edges: .bottom)
        )
        
        return base
            .searchable(
                text: $searchQuery,
                isPresented: $searchPresented,
                placement: .navigationBarDrawer
            )
    }
    
    @ViewBuilder
    private func mediaView(
        viewType: ViewType,
        items: [ArrMedia],
        onItemClicked: @escaping (ArrMedia) -> Void,
        itemIsActive: @escaping (ArrMedia) -> Bool
    ) -> some View {
        switch viewType {
        case .grid:
            PosterGridView(items: items, onItemClick: onItemClicked, itemIsActive: itemIsActive)
        case .list:
            PosterListView(items: items, onItemClick: onItemClicked, itemIsActive: itemIsActive)
        }
    }
}
