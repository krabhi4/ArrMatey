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
        if state.items.isEmpty && searchQuery.isEmpty {
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
        VStack(spacing: 0) {
            if items.isEmpty {
                EmptySearchResultsView(type: type, query: searchQuery, onShouldSearch: {
                    navigation.go(to: .search(searchQuery), of: type)
                })
            } else {
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
            }
        }
        .id(items.count)
        .searchable(
            text: $searchQuery,
            isPresented: $searchPresented,
            placement: .automatic
        )
    }
    
    @ViewBuilder
    private func mediaView(
        viewType: ViewType,
        items: [ArrMedia],
        onItemClicked: @escaping (ArrMedia) -> Void,
        itemIsActive: @escaping (ArrMedia) -> Bool
    ) -> some View {
        ScrollView {
            if viewType == .grid {
                let columns = [GridItem(.adaptive(minimum: 120), spacing: 16)]
                
                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(items, id: \.id) { item in
                        PosterItem(item: item) {
                            VStack {
                                Spacer()
                                if item.id != nil {
                                    ProgressView(value: Double(item.statusProgress))
                                        .tint(itemIsActive(item) ? Color.blue : Color(argb: item.statusColor))
                                        .padding(8)
                                }
                            }
                        }
                        .onTapGesture {
                            onItemClicked(item)
                        }
                    }
                }
                .padding(16)
            } else {
                LazyVStack(spacing: 12) {
                    ForEach(items, id: \.id) { item in
                        MediaItemView(item: item, isActive: itemIsActive(item))
                            .onTapGesture {
                                onItemClicked(item)
                            }
                    }
                }
                .padding(16)
            }
        }
    }
}
