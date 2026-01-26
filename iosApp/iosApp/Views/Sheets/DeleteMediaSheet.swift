//
//  DeleteMediaSheet.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-25.
//

import SwiftUI

struct DeleteMediaSheet: View {
    let isLoading: Bool
    let onConfirm: (_ addExclusion: Bool, _ deleteFiles: Bool) -> Void
    
    @State private var addExclusion: Bool = false
    @State private var deleteFiles: Bool = false
    
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Toggle("add_exclusion", isOn: $addExclusion)
                } footer: {
                    Text("add_exclusion_description")
                }
                Section {
                    Toggle("delete_files", isOn: $deleteFiles)
                } footer: {
                    Text("delete_files_description")
                }
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button {
                        dismiss()
                    } label: {
                        Label("cancel", systemImage: "xmark")
                    }
                    .tint(.primary)
                }
                ToolbarItem(placement: .primaryAction) {
                    Button(role: .destructive) {
                        onConfirm(addExclusion, deleteFiles)
                    } label: {
                        if isLoading {
                            ProgressView().tint(.white)
                        } else {
                            Label("delete", systemImage: "trash")
                        }
                    }
                    .tint(.red)
                }
            }
        }
    }
}
