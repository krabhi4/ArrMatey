//
// Created by Owen LeJeune on 2025-12-02.
//

import Foundation
import SwiftUI
import Shared

struct ArrTabView: View {
    let type: InstanceType

    @StateObject private var networkConnectivityViewModel = NetworkConnectivityViewModel()
//    @StateObject private var networkViewModel = NetworkConnectivityViewModel()
//    @StateObject private var instanceViewModel = InstanceView
//    @StateObject private var arrViewModel = ArrLibraryViewMo
    
    @State private var selectedSortOptions: SortBy = .title
    @State private var selectedSortOrder: Shared.SortOrder = .asc
    @State private var selectedFilter: FilterBy = .all
    
    @State private var hasServerConnectivityError: Bool = false
    @State private var showServerError: Bool = false
    @State private var showNetworkError: Bool = false
    @State private var showGenericError: Bool = false
    @State private var errorMessage: String = ""
    
    var body: some View {
        NavigationStack {
//            if let instance = 
        }
    }
}
