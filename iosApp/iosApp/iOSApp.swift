import SwiftUI
import UserNotifications
import ComposeApp

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
        // Устанавливаем делегат для обработки уведомлений
        UNUserNotificationCenter.current().delegate = self
        return true
    }
    
    // Обработка нажатия на уведомление когда приложение в фоне или закрыто
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // Устанавливаем флаг для открытия экрана перерыва
        MainViewControllerKt.shouldOpenBreakNotificationScreen = true
        completionHandler()
    }
    
    // Обработка уведомления когда приложение на переднем плане
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        // Показываем уведомление даже когда приложение активно
        completionHandler([.banner, .sound, .badge])
    }
}