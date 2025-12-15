//
//  PosterGridView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

struct PosterGridView: UIViewControllerRepresentable {
    let items: [AnyArrMedia]
    let onItemClick: (AnyArrMedia) -> Void
    
    init(
        items: [AnyArrMedia],
        onItemClick: @escaping (AnyArrMedia) -> Void
    ) {
        self.items = items
        self.onItemClick = onItemClick
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let vc = PosterGridViewController(items: self.items, onItemClick: self.onItemClick)
        vc.view.backgroundColor = .clear
        return vc
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        // left empty
    }
    
    func sizeThatFits(_ proposal: ProposedViewSize, uiViewController: UIViewControllerType, context: Context) -> CGSize? {
        return proposal.replacingUnspecifiedDimensions()
    }
}
