//
//  HeaderItemView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-15.
//

import Shared
import SwiftUI

struct HeaderItemView: View {
    @Binding var header: InstanceHeader
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 24) {
                Text(MR.strings().header_name.localized()).layoutPriority(2)
                TextField(
                    text: Binding(
                        get: { header.key },
                        set: { header = InstanceHeader(key: $0, value: header.value) }
                    ),
                    prompt: Text("X-Custom-Header")
                ) {
                    EmptyView()
                }
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .multilineTextAlignment(.trailing)
            }
            
            HStack(spacing: 12) {
                Text(MR.strings().header_value.localized()).layoutPriority(2)
                TextField(
                    text: Binding(
                        get: { header.value },
                        set: { header = InstanceHeader(key: header.key, value: $0) }
                    ),
                    prompt: Text("value")
                ) {
                    EmptyView()
                }
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .multilineTextAlignment(.trailing)
            }
        }
        .padding(.vertical, 4)
    }
}
