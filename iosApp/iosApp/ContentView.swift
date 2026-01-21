import SwiftUI
import Shared

struct ContentView: View {
    @EnvironmentObject var navigationManager: NavigationManager

    var body: some View {
        TabView(selection: $navigationManager.selectedTab) {
            ForEach(TabItem.companion.allValues(), id: \.self) { tabItem in
                Tab(LocalizedStringKey(tabItem.textKey), systemImage: tabItem.iosIcon, value: tabItem) {
                    BottomTabView(tabItem: tabItem)
                }
            }
        }
        .tabViewStyle(.sidebarAdaptable)
        .apply {
            if #available(iOS 26, *) {
                $0.tabBarMinimizeBehavior(.never)
            }
        }
    }
}
