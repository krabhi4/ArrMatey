import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        SonarrConfigScreenComposable()
            .ignoresSafeArea()
    }

    // @State private var showContent = false
    // @ObservedObject private(set) var viewModel: ViewModel
    //
    // var body: some View {
    //     VStack {
    //         Button("Click me!") {
    //             withAnimation {
    //                 showContent = !showContent
    //             }
    //         }
    //
    //         if showContent {
    //             VStack(spacing: 16) {
    //                 Image(systemName: "swift")
    //                     .font(.system(size: 200))
    //                     .foregroundColor(.accentColor)
    //                 ListView(phrases: viewModel.greetings)
    //                     .task { await self.viewModel.startObserving() }
    //             }
    //             .transition(.move(edge: .top).combined(with: .opacity))
    //         }
    //     }
    //     .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    //     .padding()
    // }
}

struct SonarrConfigScreenComposable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        SonarrConfigurationScreenViewControllerKt.SonarrConfigurationScreenViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}

// extension ContentView {
//     @MainActor
//     class ViewModel : ObservableObject {
//         @Published var greetings: Array<String> = []
//
//         func startObserving() async {
//             for await phrase in Greeting().greet() {
//                 self.greetings.append(phrase)
//             }
//         }
//     }
// }
//
// struct ListView : View {
//     let phrases: Array<String>
//
//     var body: some View {
//         List(phrases, id: \.self) {
//             Text($0)
//         }
//     }
// }
