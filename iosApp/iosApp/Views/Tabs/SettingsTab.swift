//
// Created by Owen LeJeune on 2025-11-20.
//

import Foundation
import SwiftUI
import Shared

struct SettingsTab: View {
    var body: some View {
        SonarrConfigScreenComposable()
    }
}

struct SonarrConfigScreenComposable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SonarrConfigurationScreenViewControllerKt.SonarrConfigurationScreenViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}