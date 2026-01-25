//
//  ActivityTab.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-21.
//

import SwiftUI
import Shared

struct ActivityTab: View {
    
    @ObservedObject private var viewModel = ActivityQueueViewModelS()
    
    @State private var selectedItem: IdentifiableQueueItem? = nil
    
    private var titleText: String {
        guard !viewModel.queueItems.isEmpty else { return String(localized: LocalizedStringResource("activity")) }
        return "\(String(localized: LocalizedStringResource("activity"))) (\(viewModel.queueItems.count))"
    }
    
    var body: some View {
        queueItemContent
            .navigationTitle(titleText)
            .toolbar {
                if viewModel.isPolling {
                    ToolbarItem(placement: .primaryAction) {
                        ProgressView().progressViewStyle(.circular)
                    }
                }
            }
            .sheet(item: $selectedItem) { wrapper in
                QueueItemInfoSheet(item: wrapper.item)
                    .presentationDetents([.fraction(0.7)])
            }
    }
    
    @ViewBuilder
    private var queueItemContent: some View {
        if viewModel.queueItems.isEmpty {
            emptyActivityView
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            ScrollView {
                VStack(spacing: 12) {
                    ForEach(viewModel.queueItems, id: \.id) { item in
                        ActivityQueueItem(item: item, onClick: { selectedItem = IdentifiableQueueItem(item: item) })
                    }
                    if viewModel.queueItems.isEmpty {
                        emptyActivityView
                    }
                }
                .padding(.vertical, 12)
                .padding(.horizontal, 18)
            }
        }
    }
    
    @ViewBuilder
    private var emptyActivityView: some View {
        VStack(alignment: .center, spacing: 12) {
            Image(systemName: "square.and.arrow.down.fill")
                .font(.system(size: 64))
                .foregroundStyle(.secondary)
            Text(LocalizedStringResource("no_activity"))
                .font(.system(size: 20, weight: .bold))
        }
        .padding(.horizontal, 24)
    }
    
}
