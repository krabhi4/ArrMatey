//
//  FlowHelpers.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-21.
//

import Shared
import SwiftUI

extension SkieSwiftStateFlow {
    func observeAsync(_ consumer: @escaping (_ emission: T) -> Void) {
        Task { @MainActor in
            for try await value in self {
                consumer(value)
            }
        }
    }
}

extension SkieSwiftOptionalStateFlow {
    func observeAsync(_ consumer: @escaping (_ emission: T?) -> Void) {
        Task { @MainActor in
            for try await value in self {
                consumer(value)
            }
        }
    }
}

extension SkieSwiftFlow {
    func observeAsync(_ consumer: @escaping (_ emission: T) -> Void) {
        Task { @MainActor in
            for try await value in self {
                consumer(value)
            }
        }
    }
}
