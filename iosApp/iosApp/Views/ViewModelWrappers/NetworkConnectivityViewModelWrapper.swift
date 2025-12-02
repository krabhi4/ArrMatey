//
// Created by Owen LeJeune on 2025-12-02.
//

import Foundation
import Shared
import Combine

class NetworkConnectivityViewModel: ObservableObject {
    @Published var isConnected: Bool = false

    private let viewModel = NetworkConnectivityRepository()
    private var task: Task<Void, Never>?

    func startObserving() {
        task = Task {
            for await value in viewModel.isConnected {
                await MainActor.run {
                    self.isConnected = value as! Bool
                }
            }
        }
        viewModel.startObserving()
    }

    func stopObserving() {
        task?.cancel()
        viewModel.stopObserving()
    }
}
