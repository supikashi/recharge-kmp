import SwiftUI
import UserNotifications
import ComposeApp
import FirebaseCore

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        
        UNUserNotificationCenter.current().delegate = self
        stopExpiredLiveActivitiesIfNeeded()
        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        stopExpiredLiveActivitiesIfNeeded()
    }
    
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        
        let userInfo = response.notification.request.content.userInfo
        let isStartNotification = userInfo["is_start_notification"] as? Bool ?? false
        
        if isStartNotification {
            MainViewControllerKt.shouldOpenBreakNotificationScreen = true
        }
        completionHandler()
    }
    
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        completionHandler([.banner, .sound, .badge])
    }

    private func stopExpiredLiveActivitiesIfNeeded() {
        guard #available(iOS 16.1, *) else { return }

        _Concurrency.Task { @MainActor in
            await BreakTimerLiveActivityController.shared.stopExpiredActivitiesIfNeeded()
        }
    }
}
