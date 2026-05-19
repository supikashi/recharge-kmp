import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    private let timerBackgroundService = LiveActivityTimerBackgroundService()

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(timerBackgroundService: timerBackgroundService)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(edges: .all)
            .ignoresSafeArea()
    }
}


