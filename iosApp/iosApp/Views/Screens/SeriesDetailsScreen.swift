//
//  SeriesDetailsScreen.swift
//  iosApp
//
//  Created by Owen LeJeune on 2025-12-15.
//


import SwiftUI
import Shared

struct SeriesDetailsScreen: View {
//    let id: Int
//    
//    private let type: InstanceType = .sonarr
//    
//    @ObservedObject private var instanceViewModel = InstanceViewModel()
//    
//    @State private var arrViewModel: ArrViewModel? = nil
//    @State private var detailUiState: Any = DetailsUiStateInitial()
//    @State private var observationTask: Task<Void, Never>? = nil
//    
//    private var firstInstance: Instance? {
//        instanceViewModel.firstInstance
//    }
//    
//    private var isMonitored: Bool {
//        if let state = detailUiState as? DetailsUiStateSuccess<ArrSeries> {
//            return state.item?.monitored == true
//        }
//        return false
//    }
    
    var body: some View {
//        contentForState()
//            .task {
//                await setupViewModel()
//                if let vm = arrViewModel {
//                    await vm.getDetails(id: Int32(id))
//                }
//            }
//            .onDisappear {
//                observationTask?.cancel()
//            }
    }
//    
//    @ViewBuilder
//    private func header(item: ArrSeries) -> some View {
//        HStack(alignment: .top, spacing: 12) {
//            PosterItemView(
//                item: item,
//                onItemClick: {_ in }
//            )
//            .frame(width: 150, height: 220)
//            
//            VStack(alignment: .leading, spacing: 4) {
//                Text(item.title)
//                    .font(.system(size: 38, weight: .medium))
//                    .lineLimit(3)
//                    .truncationMode(.tail)
//                
//                Text([String(item.year), item.runtimeString, item.certification ?? "NA"].joined(separator: " • "))
//                    .font(.system(size: 16))
//
//                Text([item.releasedBy ?? "", item.statusString].joined(separator: " • "))
//                    .font(.system(size: 14))
//                    .lineSpacing(16 - 14) // optional rough equivalent of lineHeight
//
//            }
//            .frame(alignment: .top)
//        }
//    }
//    
//    @ViewBuilder
//    private func contentForState() -> some View {
//        switch detailUiState {
//        case is DetailsUiStateInitial:
//            ZStack {
//                Text("initial state")
//            }
//        case is DetailsUiStateLoading:
//            ZStack {
//                ProgressView()
//                    .progressViewStyle(.circular)
//            }
//        case let state as DetailsUiStateSuccess<AnyArrMedia>:
//            if let item = state.item {
//                VStack(alignment: .leading, spacing: 12){
//                    header(item: item)
//                    
//                    item.make
//                }
//                .padding(.horizontal, 12)
//                .frame(
//                    maxWidth: .infinity,
//                    maxHeight: .infinity,
//                    alignment: .top // or .topLeading
//                )
//            }
//        case let error as DetailsUiStateError<GenericArrMedia>:
//            VStack{}
//        default:
//            VStack {
//                Text("detault")
//            }
//        }
//    }
//    
//    @MainActor
//    private func setupViewModel() async {
//        await instanceViewModel.getFirstInstance(instanceType: type)
//        
//        guard let firstInstance = self.firstInstance else { return }
//        
//        self.arrViewModel = ArrViewModel(instance: firstInstance)
//        
//        observationTask?.cancel()
//        observationTask = Task {
//            await observeUiState()
//        }
//    }
//    
//    @MainActor
//    private func observeUiState() async {
//        guard let viewModel = arrViewModel else { return }
//        
//        do {
//            let flow = viewModel.getDetailsUiState()
//            for try await state in flow {
//                self.detailUiState = state
//            }
//        } catch {
//            print("Error observing state: \(error)")
//        }
//    }
}
