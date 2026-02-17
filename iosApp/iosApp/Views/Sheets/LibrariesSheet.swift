//
//  LibrariesSheet.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-16.
//

import Shared
import SwiftUI

struct LibrariesSheet: View {
    
    @State private var libraries: [Library] = []
    @State private var licenses: [String:License] = [:]
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                ForEach(libraries, id: \.self) { library in
                    LibraryRow(library: library, licenses: licenses)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 10)
                }
            }
            .navigationTitle(MR.strings().libraries.localized())
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigation) {
                    Button {
                        dismiss()
                    } label: {
                        Label(MR.strings().close.localized(), systemImage: "xmark")
                            .foregroundStyle(.white)
                    }
                    .tint(nil)
                }
            }
        }
        .onAppear {
            loadLibraries()
        }
    }
    
    private func loadLibraries() {
        guard let url = Bundle.main.url(forResource: "aboutLibraries", withExtension: "json") else { return }
        guard let data = try? Data(contentsOf: url) else { return }
        guard let response = try? JSONDecoder().decode(LibrariesResponse.self, from: data) else { return }
    
        libraries = response.libraries
        licenses = response.licenses
    }
    
}
