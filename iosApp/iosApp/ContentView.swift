import SwiftUI
import Shared

struct ContentView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @ObservedObject private var queueViewModel = ActivityQueueViewModelS()
    @ObservedObject private var preferences = PreferencesViewModel()
    
    @State private var showLauncher: Bool = false

    var body: some View {
        TabView(selection: $navigationManager.selectedTab) {
            ForEach(preferences.tabPreferences.bottomTabItems, id: \.self) { tabItem in
                NavigationStack(path: $navigationManager.settingsPath) {
                    TabItemContent(tabItem: tabItem)
                        .toolbar { toolbarItem }
                }
                .id(tabItem.name)
                .tabItem {
                    Label(tabItem.resource.localized(), systemImage: tabItem.iosIcon)
                }
                .tag(tabItem)
                .badge(badgeValue(for: tabItem))
                .toolbar(preferences.tabPreferences.bottomTabItems.count <= 1 ? .hidden : .visible, for: .tabBar)
            }
        }
        .tabViewStyle(.sidebarAdaptable)
        .fullScreenCover(isPresented: $showLauncher) {
            AppLauncherGrid(showLauncher: $showLauncher)
                .environmentObject(navigationManager)
        }
    }

    private func badgeValue(for tabItem: TabItem) -> Int {
        tabItem == .activity ? Int(queueViewModel.tasksWithIssues) : 0
    }
    
    private var toolbarItem: some ToolbarContent {
        ToolbarItem(placement: .topBarLeading) {
            Button {
                showLauncher = true
            } label: {
                Image(systemName: "line.3.horizontal")
            }
        }
    }
}

struct AppLauncherGrid: View {
    @Binding var showLauncher: Bool
    @ObservedObject private var preferences = PreferencesViewModel()
    @EnvironmentObject private var navigationManager: NavigationManager

    private let columns = [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())]
    
    private var hasLauncherContent: Bool {
        preferences.tabPreferences.hiddenTabs.count > 0
    }

    var body: some View {
        NavigationStack(path: $navigationManager.launcherPath) {
            ScrollView {
                launcherContent
            }
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: {
                        showLauncher = false
                    }) {
                        Image(systemName: "xmark")
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    NavigationLink(value: TabItem.settings) {
                        Image(systemName: "gearshape.fill")
                    }
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .navigationDestination(for: TabItem.self) { item in
                TabItemContent(tabItem: item)
            }
        }
    }
    
    private var launcherContent: some View {
        LazyVGrid(columns: columns, spacing: 25) {
            ForEach(preferences.tabPreferences.hiddenTabs, id: \.self) { item in
                NavigationLink(value: item) {
                    VStack(spacing: 12) {
                        Image(systemName: item.iosIcon)
                            .font(.system(size: 30))
                            .frame(width: 65, height: 65)
                            .background(Color.accentColor.opacity(0.1))
                            .cornerRadius(16)
                        
                        Text(item.resource.localized())
                            .font(.caption)
                            .foregroundColor(.primary)
                    }
                }
            }
        }
        .padding(25)
    }
}
