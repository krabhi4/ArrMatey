//
//  MediaInfoArea.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import SwiftUI
import Shared

struct MediaInfoArea: View {
    let item: AnyArrMedia
    
    @State private var infoItems: [Info] = []
    
    var body: some View {
        Section {
            VStack(spacing: 12) {
                ForEach(infoItems, id: \.self) { info in
                    HStack(alignment: .center) {
                        Text(info.label)
                            .font(.system(size: 14))
                        Spacer()
                        Text(info.value)
                            .font(.system(size: 14))
                            .foregroundColor(.accentColor)
                            .lineLimit(1)
                            .truncationMode(.tail)
                            .multilineTextAlignment(.trailing)
                            .frame(maxWidth: .infinity, alignment: .trailing)
                    }
                    
                    if info != infoItems.last {
                        Divider()
                    }
                }
            }
            .collect(flow: item.infoItems, into: $infoItems)
        } header: {
            Text(String(localized: LocalizedStringResource("information")))
                .font(.system(size: 20, weight: .bold))
        }
    }
}
