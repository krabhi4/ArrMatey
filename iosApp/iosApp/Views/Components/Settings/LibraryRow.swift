//
//  LibraryRow.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-16.
//

import Shared
import SwiftUI

struct LibraryRow: View {
    let library: Library
    let licenses: [String:License]
    @State private var isExpanded = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(library.name)
                        .font(.headline)
                    
                    if let author = library.organization?.name {
                        Text(author)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    
                    if let version = library.artifactVersion {
                        Text("v\(version)")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                
                Spacer()
                
                if let libLicenses = library.licenses, !libLicenses.isEmpty {
                    ForEach(libLicenses, id: \.self) { licenseKey in
                        if let license = licenses[licenseKey] {
                            Text(license.name)
                                .font(.caption)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(Color.blue.opacity(0.2))
                                .cornerRadius(8)
                        }
                    }
                }
            }
            
            if isExpanded {
                VStack(alignment: .leading, spacing: 8) {
                    if let description = library.description {
                        Text(description)
                            .font(.body)
                            .foregroundColor(.secondary)
                            .padding(.top, 4)
                    }
                    
                    if let website = library.website, let url = URL(string: website) {
                        Link("Website", destination: url)
                            .font(.subheadline)
                    }
                    
                    if let libLicenses = library.licenses, !libLicenses.isEmpty {
                        ForEach(libLicenses, id: \.self) { licenseKey in
                            if let license = licenses[licenseKey], let licenseContent = license.licenseContent {
                                DisclosureGroup("License Details") {
                                    ScrollView {
                                        Text(licenseContent)
                                            .font(.system(.caption, design: .monospaced))
                                            .padding()
                                    }
                                    .frame(maxHeight: 200)
                                }
                            }
                        }
                    }
                }
                .padding(.top, 8)
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                isExpanded.toggle()
            }
        }
    }
}
