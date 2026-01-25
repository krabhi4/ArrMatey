//
//  PosterGridView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-03.
//

import SwiftUI
import Shared

struct PosterGridView: UIViewControllerRepresentable {
    private let items: [ArrMedia]
    private let onItemClick: (ArrMedia) -> Void
    private let itemIsActive: (ArrMedia) -> KotlinBoolean
    
    init(
        items: [ArrMedia],
        onItemClick: @escaping (ArrMedia) -> Void,
        itemIsActive: @escaping (ArrMedia) -> Bool
    ) {
        self.items = items
        self.onItemClick = onItemClick
        self.itemIsActive = { item in
            itemIsActive(item).asKotlinBool
        }
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let vc = PosterGridViewController(items: self.items, itemIsActive: self.itemIsActive, onItemClick: self.onItemClick)
        vc.view.backgroundColor = .clear
        return vc
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        // left blank
    }
    
    func sizeThatFits(_ proposal: ProposedViewSize, uiViewController: UIViewControllerType, context: Context) -> CGSize? {
        return proposal.replacingUnspecifiedDimensions()
    }
}
