//
//  Chip.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-16.
//

import SwiftUI

struct Chip: View {
    let title: String
    let icon: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 14))
                Text(title)
                    .font(.caption)
                    .fontWeight(.medium)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Capsule().stroke(Color.secondary.opacity(0.3)))
        }
        .buttonStyle(.plain)
    }
}
