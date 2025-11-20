import SwiftUI
import Shared

struct ContentView: View {
    @State private var selectedTab: TabItem = TabItem.shows

    var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(TabItem.companion.allValues(), id: \.self) { tabItem in
                Tab(tabItem.textKey, systemImage: tabItem.iosIcon, value: tabItem) {
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