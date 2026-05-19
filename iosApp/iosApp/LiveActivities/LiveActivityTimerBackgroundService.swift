import ActivityKit
import ComposeApp
import Foundation

final class LiveActivityTimerBackgroundService: NSObject, TimerBackgroundService {
    func start(endTimeMillis: Int64) {
        guard #available(iOS 16.1, *) else { return }

        _Concurrency.Task { @MainActor in
            BreakTimerLiveActivityController.shared.start(endTimeMillis: endTimeMillis)
        }
    }

    func stop() {
        guard #available(iOS 16.1, *) else { return }

        _Concurrency.Task { @MainActor in
            await BreakTimerLiveActivityController.shared.stop()
        }
    }
}

@available(iOS 16.1, *)
@MainActor
final class BreakTimerLiveActivityController {
    static let shared = BreakTimerLiveActivityController()

    private var autoEndTimer: Timer?

    func start(endTimeMillis: Int64) {
        let targetDate = Date(timeIntervalSince1970: TimeInterval(endTimeMillis) / 1_000)
        guard targetDate > Date() else {
            _Concurrency.Task { await stop() }
            return
        }

        _Concurrency.Task {
            await stop()

            guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }

            let attributes = BreakTimerActivityAttributes(title: "BreakLab")
            let state = BreakTimerActivityAttributes.ContentState(endTimeMillis: endTimeMillis)

            do {
                BreakTimerLiveActivityStore.endTimeMillis = endTimeMillis

                if #available(iOS 16.2, *) {
                    let content = ActivityContent(state: state, staleDate: nil, relevanceScore: 0)
                    _ = try Activity.request(
                        attributes: attributes,
                        content: content,
                        pushType: nil
                    )
                } else {
                    _ = try Activity.request(
                        attributes: attributes,
                        contentState: state,
                        pushType: nil
                    )
                }

                scheduleAutoEnd(at: targetDate)
            } catch {
                BreakTimerLiveActivityStore.endTimeMillis = nil
                print("Failed to start Live Activity: \(error.localizedDescription)")
            }
        }
    }

    func stop() async {
        autoEndTimer?.invalidate()
        autoEndTimer = nil
        BreakTimerLiveActivityStore.endTimeMillis = nil

        let nowMillis = Int64(Date().timeIntervalSince1970 * 1_000)
        let finalState = BreakTimerActivityAttributes.ContentState(endTimeMillis: nowMillis)

        for activity in Activity<BreakTimerActivityAttributes>.activities {
            await end(activity, finalState: finalState)
        }
    }

    func stopExpiredActivitiesIfNeeded() async {
        let nowMillis = Int64(Date().timeIntervalSince1970 * 1_000)
        let activities = Activity<BreakTimerActivityAttributes>.activities
        let expiredActivities = activities.filter { $0.contentState.endTimeMillis <= nowMillis }
        let futureEndMillis = activities
            .map(\.contentState.endTimeMillis)
            .filter { $0 > nowMillis }
            .min()

        for activity in expiredActivities {
            let finalState = BreakTimerActivityAttributes.ContentState(endTimeMillis: nowMillis)
            await end(activity, finalState: finalState)
        }

        if let futureEndMillis {
            BreakTimerLiveActivityStore.endTimeMillis = futureEndMillis
            scheduleAutoEnd(at: Date(timeIntervalSince1970: TimeInterval(futureEndMillis) / 1_000))
        } else if !expiredActivities.isEmpty || activities.isEmpty {
            BreakTimerLiveActivityStore.endTimeMillis = nil
        }
    }

    private func scheduleAutoEnd(at targetDate: Date) {
        autoEndTimer?.invalidate()

        let delay = max(0, targetDate.timeIntervalSinceNow)
        guard delay > 0 else {
            _Concurrency.Task { [weak self] in
                await self?.stop()
            }
            return
        }

        let timer = Timer(fire: targetDate, interval: 0, repeats: false) { [weak self] _ in
            _Concurrency.Task { @MainActor in
                await self?.stop()
            }
        }
        RunLoop.main.add(timer, forMode: .common)
        autoEndTimer = timer
    }

    private func end(
        _ activity: Activity<BreakTimerActivityAttributes>,
        finalState: BreakTimerActivityAttributes.ContentState
    ) async {
        if #available(iOS 16.2, *) {
            let content = ActivityContent(state: finalState, staleDate: nil, relevanceScore: 0)
            await activity.end(content, dismissalPolicy: .immediate)
        } else {
            await activity.end(using: finalState, dismissalPolicy: .immediate)
        }
    }
}

private enum BreakTimerLiveActivityStore {
    private static let key = "break_timer_live_activity_end_time_millis"

    static var endTimeMillis: Int64? {
        get {
            guard let value = UserDefaults.standard.object(forKey: key) as? NSNumber else {
                return nil
            }
            return value.int64Value
        }
        set {
            if let newValue {
                UserDefaults.standard.set(NSNumber(value: newValue), forKey: key)
            } else {
                UserDefaults.standard.removeObject(forKey: key)
            }
        }
    }
}
