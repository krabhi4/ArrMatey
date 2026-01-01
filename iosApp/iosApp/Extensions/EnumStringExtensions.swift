//
//  EnumStringExtensions.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-31.
//

import Shared

extension MovieStatus {
    func label() -> String {
        switch self {
        case .tba: "tba"
        case .deleted: "deleted"
        case .released: "released"
        case .announced: "announced"
        case .inCinemas: "in_cinemas"
        }
    }
}

extension SeriesMonitorType {
    func label() -> String {
        switch self {
        case .unknown: "unknown"
        case .all: "all"
        case .future: "future"
        case .missing: "missing"
        case .existing: "existing"
        case .firstSeason: "first_season"
        case .lastSeason: "last_season"
        case .latestSeason: "latest_season"
        case .pilot: "pilot"
        case .recent: "recent"
        case .monitorSpecials: "monitor_specials"
        case .unmonitorSpecials: "unmonitor_specials"
        case .none: "none"
        case .skip: "skip"
        }
    }
}

extension SeriesType {
    func label() -> String {
        switch self {
        case .standard: "standard"
        case .daily: "daily"
        case .anime: "anime"
        }
    }
}
