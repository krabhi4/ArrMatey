//
//  CalendarDayCell.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-09.
//

import SwiftUI
import Shared

struct CalendarDayCell: View {
    let date: LocalDate
    let isSelected: Bool
    let movieCount: Int
    let episodeCount: Int
    let albumCount: Int
    let onClick: () -> Void
    
    private var isToday: Bool {
        let today = Calendar.current.dateComponents([.year, .month, .day], from: Date())
        return Int(date.year) == today.year &&
                Int(date.month.number()) == today.month &&
                Int(date.day) == today.day
    }
    
    var body: some View {
        Button(action: onClick) {
            VStack(spacing: 4) {
                Text("\(date.day)")
                    .font(.body)
                    .fontWeight(isToday || isSelected ? .bold : .regular)
                    .foregroundColor(isSelected ? .white : (isToday ? .accentColor : .primary))
                
                Spacer()
                
                if movieCount > 0 || episodeCount > 0 || albumCount > 0 {
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 2) {
                        if movieCount > 0 {
                            CountBadge(count: movieCount, color: .blue)
                        }
                        if episodeCount > 0 {
                            CountBadge(count: episodeCount, color: .teal)
                        }
                        if albumCount > 0 {
                            CountBadge(count: albumCount, color: .purple)
                        }
                    }
                } else {
                    Spacer()
                        .frame(height: 16)
                }
            }
            .frame(maxWidth: .infinity)
            .aspectRatio(1, contentMode: .fit)
            .padding(4)
            .background(backgroundColor)
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(isToday && !isSelected ? Color.accentColor : Color.clear, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
    
    private var backgroundColor: Color {
        if isSelected {
            return .accentColor
        } else if isToday {
            return Color.accentColor.opacity(0.15)
        } else {
            return .clear
        }
    }
}

struct CountBadge: View {
    let count: Int
    let color: Color
    
    var body: some View {
        Text(count > 9 ? "9+" : "\(count)")
            .font(.system(size: 8))
            .fontWeight(.bold)
            .foregroundColor(.white)
            .frame(width: 16, height: 16)
            .background(color)
            .clipShape(Circle())
    }
}
