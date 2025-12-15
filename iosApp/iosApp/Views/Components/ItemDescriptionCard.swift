//
//  ItemDescriptionCard.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//

import SwiftUI

struct ItemDescriptionCard: View {
    let overview: String?
    
    @State private var expanded = false
    
    var body: some View {
        if let overview {
            VStack {
                Text(overview)
                    .font(.system(size: 14))
                    .lineLimit(expanded ? nil : 10)
                    .truncationMode(.tail)
                    .transition(.slide)
                    .background()
                    .onTapGesture {
                        withAnimation(.snappy) { expanded = true }
                    }
            }
        }
    }
}
