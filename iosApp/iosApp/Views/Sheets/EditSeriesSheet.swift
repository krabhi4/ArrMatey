//
//  EditSeriesSheet.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-03.
//

import SwiftUI
import Shared

struct EditSeriesSheet: View {
    let item: ArrSeries
    let qualityProfiles: [QualityProfile]
    let rootFolders: [RootFolder]
    let tags: [Tag]
    let editInProgress: Bool
    let onEditItem: (ArrSeries, Bool) -> Void
    
    @Environment(\.dismiss) var dismiss
    
    @State private var monitored: Bool
    @State private var monitorNewSeasons: Bool
    @State private var qualityProfileId: Int32
    @State private var seriesType: SeriesType
    @State private var seasonFolders: Bool
    @State private var rootFolder: String
    
    @State private var moveFiles: Bool = false
    
    private var canMove: Bool {
        rootFolder != item.rootFolderPath
    }
    
    init(item: ArrSeries, qualityProfiles: [QualityProfile], rootFolders: [RootFolder], tags: [Tag], editInProgress: Bool, onEditItem: @escaping (ArrSeries, Bool) -> Void) {
        self.item = item
        self.qualityProfiles = qualityProfiles
        self.rootFolders = rootFolders
        self.tags = tags
        self.editInProgress = editInProgress
        self.onEditItem = onEditItem
        
        self.monitored = item.monitored
        self.monitorNewSeasons = item.monitorNewItems == .all
        self.qualityProfileId = item.qualityProfileId
        self.seriesType = item.seriesType
        self.seasonFolders = item.seasonFolder
        self.rootFolder = item.rootFolderPath ?? ""
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Toggle("monitored", isOn: $monitored)
                    Toggle("monitor_new_seasons", isOn: $monitorNewSeasons)
                    Toggle("season_folders", isOn: $seasonFolders)
                    Picker("series_type", selection: $seriesType) {
                        ForEach(SeriesType.companion.allEntries(), id: \.self) { type in
                            Text(type.label()).tag(type)
                        }
                    }
                    Picker("quality_profile", selection: $qualityProfileId) {
                        ForEach(qualityProfiles, id: \.id) { qp in
                            Text(qp.name ?? "").tag(qp.id)
                        }
                    }
                }
                
                Section {
                    if rootFolders.count > 1 {
                        Picker("root_folder", selection: $rootFolder) {
                            ForEach(rootFolders, id: \.id) { folder in
                                Text("\(folder.path) (\(folder.freeSpace.bytesAsFileSizeString()))")
                                    .tag(folder.path)
                            }
                        }
                        if canMove {
                            Toggle("Move files", isOn: $moveFiles)
                        }
                    }
                } footer: {
                    if canMove {
                        Text("Whether to automcatically move files to the new folder or not.")
                    }
                }
            }
            .toolbarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigation) {
                    Button {
                        dismiss()
                    } label: {
                        Label("close", systemImage: "xmark")
                            .foregroundStyle(.white)
                    }
                    .tint(nil)
                }
                
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        let newSeries = item.doCopyForEdit(monitored: monitored, monitorNewSeasons: monitorNewSeasons ? .all : .none, qualityProfileId: qualityProfileId, seriesType: seriesType, seasonFolder: seasonFolders, rootFolderPath: rootFolder)
                        onEditItem(newSeries, moveFiles && canMove)
                    } label: {
                        if editInProgress {
                            ProgressView()
                                .progressViewStyle(.circular)
                                .foregroundStyle(.white)
                        } else {
                            Label("save", systemImage: "checkmark")
                                .foregroundStyle(.white)
                        }
                    }
                    .tint(nil)
                }
            }
        }
    }
}
