//
//  EditMovieSheet.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-03.
//

import SwiftUI
import Shared

struct EditMovieSheet: View {
    let item: ArrMovie
    let qualityProfiles: [QualityProfile]
    let rootFolders: [RootFolder]
    let tags: [Tag]
    let editInProgress: Bool
    let onEditItem: (ArrMovie, BooleanLiteralType) -> Void
    
    @State private var monitored: Bool
    @State private var minimumAvailability: MediaStatus
    @State private var qualityProfileId: Int32
    @State private var rootFolder: String
    
    @State private var moveFiles: Bool = false
    
    private var canMove: Bool {
        rootFolder != item.rootFolderPath
    }
    
    private let statusOptions: [MediaStatus] = [.announced, .inCinemas, .released]
    
    init(item: ArrMovie, qualityProfiles: [QualityProfile], rootFolders: [RootFolder], tags: [Tag], editInProgress: Bool, onEditItem: @escaping (ArrMovie, Bool) -> Void) {
        self.item = item
        self.qualityProfiles = qualityProfiles
        self.rootFolders = rootFolders
        self.tags = tags
        self.editInProgress = editInProgress
        self.onEditItem = onEditItem
    
        self.monitored = item.monitored
        self.minimumAvailability = item.minimumAvailability
        self.qualityProfileId = item.qualityProfileId
        self.rootFolder = item.rootFolderPath
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Toggle("monitored", isOn: $monitored)
                    
                    Picker("quality_profile", selection: $qualityProfileId) {
                        ForEach(qualityProfiles, id: \.id) { qp in
                            Text(qp.name ?? "").tag(qp.id)
                        }
                    }
                    
                    Picker("minimum_availability", selection: $minimumAvailability) {
                        ForEach(statusOptions, id: \.self) { status in
                            Text(status.name).tag(status)
                        }
                    }
                }
                
                Section {
                    if rootFolders.count > 1 {
                        Picker("root_rolder", selection: $rootFolder) {
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
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        let newMovie = item.doCopyForUpdate(monitored: monitored, minimumAvailability: minimumAvailability, qualityProfileId: qualityProfileId, rootFolderPath: rootFolder)
                        onEditItem(newMovie, moveFiles && canMove)
                    } label: {
                        if editInProgress {
                            ProgressView()
                                .progressViewStyle(.circular)
                        } else {
                            Label("save", systemImage: "checkmark")
                                .foregroundStyle(.white)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.primary)
                }
            }
        }
    }
}
