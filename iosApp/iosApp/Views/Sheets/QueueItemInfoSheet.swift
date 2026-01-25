//
//  QueueItemInfoSheet.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-21.
//

import Shared
import SwiftUI
import Flow

struct QueueItemInfoSheet: View {
    let item: QueueItem
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Spacer(minLength: 12)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(item.titleLabel)
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(.accentColor)
                    
                    Text(item.title ?? String(localized: "unknown"))
                        .font(.system(size: 18, weight: .semibold))
                }
                
                HStack(spacing: 4) {
                    Text(item.statusLabel)
                        .fontWeight(.medium)
                        .foregroundColor(.secondary)
                    
                    Text("• \(item.quality.qualityLabel) • \(formatBytes(item.size))")
                }
                .font(.subheadline)
                
                let chipItems = ([item.scoreLabel].compactMap { $0 }) + item.customFormats.map { $0.name }
                HFlow {
                    ForEach(chipItems, id: \.self) { chip in
                        Text(chip)
                            .font(.system(size: 12))
                            .padding(.vertical, 2)
                            .padding(.horizontal, 6)
                            .overlay(RoundedRectangle(cornerRadius: 8)
                                .stroke(.secondary.opacity(0.3), lineWidth: 1))
                    }
                }
                .padding(.bottom, 2)
                
                if let errorMessage = item.errorMessage {
                    errorCard(errorMessage)
                } else {
                    ForEach(item.statusMessages, id: \.self) { status in
                        statusCard(status)
                    }
                }
                
                Divider().padding(.vertical, 8)
                
                let infoItems = getInfoItems()
                LazyVGrid(columns: [GridItem(.flexible(), alignment: .leading), GridItem(.flexible(), alignment: .leading)], spacing: 12) {
                    ForEach(infoItems, id: \.key) { key, value in
                        if let value = value {
                            Text(LocalizedStringKey(key))
                                .font(.system(size: 14, weight: .semibold))
                            Text(value)
                                .font(.system(size: 14))
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
//        .presentationDetents([.large])
        .presentationDragIndicator(.visible)
    }
    
    @ViewBuilder
    private func errorCard(_ message: String) -> some View {
        Text(message)
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(.red.opacity(0.15))
            .foregroundColor(.red)
            .cornerRadius(12)
    }
    
    @ViewBuilder
    private func statusCard(_ status: QueueStatusMessage) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            if let title = status.title {
                Text(title)
                    .font(.system(size: 14, weight: .semibold))
            }
            ForEach(status.messages, id: \.self) { message in
                Text(message)
                    .font(.system(size: 14))
                    .italic()
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(12)
    }
    
    private func getInfoItems() -> [(key: String, value: String?)] {
        var items: [(key: String, value: String?)] = []
        items.append((key: "protocol", value: item.protocol.name))
        items.append((key: "download_client", value: item.downloadClient))
        items.append((key: "indexer", value: item.indexer))
        if !item.languageLabels.isEmpty {
            items.append((key: "languages", value: item.languageLabels.joined(separator: ", ")))
        }
        items.append((key: "added", value: item.added.description))
        items.append((key: "destination", value: item.outputPath))
        return items
    }
    
    private func formatBytes(_ size: Float) -> String {
        ByteCountFormatter.string(fromByteCount: Int64(size), countStyle: .file)
    }
}
