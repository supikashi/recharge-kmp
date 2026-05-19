import ActivityKit
import Foundation

@available(iOS 16.1, *)
public struct BreakTimerActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        public let endTimeMillis: Int64

        public init(endTimeMillis: Int64) {
            self.endTimeMillis = endTimeMillis
        }

        public var targetDate: Date {
            Date(timeIntervalSince1970: TimeInterval(endTimeMillis) / 1_000)
        }
    }

    public let title: String

    public init(title: String) {
        self.title = title
    }
}
