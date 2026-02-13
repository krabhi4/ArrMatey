import SwiftUI
import Shared

struct ContentView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @ObservedObject private var queueViewModel = ActivityQueueViewModelS()

    var body: some View {
        ZStack {
            if let drawerTab = navigationManager.selectedDrawerTab {
                NavigationStack {
                    TabItemContent(tabItem: drawerTab)
                }
                .transition(.move(edge: .trailing).combined(with: .opacity))
            } else {
                TabView(selection: $navigationManager.selectedTab) {
                    ForEach(TabItem.companion.bottomEntries, id: \.self) { tabItem in
                        Tab(LocalizedStringKey(tabItem.resource.localized()),
                            systemImage: tabItem.iosIcon,
                            value: tabItem) {
                            BottomTabView(tabItem: tabItem)
                        }
                        .badge(badgeValue(for: tabItem))
                    }
                }
                .tabViewStyle(.sidebarAdaptable)
                .transition(.move(edge: .leading).combined(with: .opacity))
            }
        }
        .animation(.spring(response: 0.35, dampingFraction: 0.8), value: navigationManager.selectedDrawerTab)
    }

    private func badgeValue(for tabItem: TabItem) -> Int {
        tabItem == .activity ? Int(queueViewModel.tasksWithIssues) : 0
    }
}
