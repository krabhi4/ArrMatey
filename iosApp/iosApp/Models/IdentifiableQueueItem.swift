//
//  IdentifiableQueueItem.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-25.
//

import Shared

struct IdentifiableQueueItem: Identifiable {
    let item: any QueueItem
    var id: String { item.id.description }
}
