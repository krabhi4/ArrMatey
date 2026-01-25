import SwiftUI
import Shared

struct ContentView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    
    @ObservedObject private var queueViewModel = ActivityQueueViewModelS()

    var body: some View {
        TabView(selection: $navigationManager.selectedTab) {
            ForEach(TabItem.companion.allValues(), id: \.self) { tabItem in
                Tab(LocalizedStringKey(tabItem.textKey), systemImage: tabItem.iosIcon, value: tabItem) {
                    BottomTabView(tabItem: tabItem)
                }
                .badge(badgeValue(for: tabItem))
            }
        }
        .tabViewStyle(.sidebarAdaptable)
        .apply {
            if #available(iOS 26, *) {
                $0.tabBarMinimizeBehavior(.never)
            }
        }
    }
    
    private func badgeValue(for tabItem: TabItem) -> Int {
        switch tabItem {
        case .activity:
            queueViewModel.tasksWithIssues
        default: 0
        }
    }
}
