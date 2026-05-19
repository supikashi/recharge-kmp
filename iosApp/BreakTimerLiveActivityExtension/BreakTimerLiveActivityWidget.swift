import ActivityKit
import SwiftUI
import WidgetKit

@main
struct BreakTimerLiveActivityBundle: WidgetBundle {
    var body: some Widget {
        BreakTimerLiveActivityWidget()
    }
}

struct BreakTimerLiveActivityWidget: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: BreakTimerActivityAttributes.self) { context in
            BreakTimerLockScreenView(context: context)
                .activityBackgroundTint(Color(.systemBackground))
                .activitySystemActionForegroundColor(.primary)
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.bottom) {
                    DynamicIslandExpandedTimer(targetDate: context.state.targetDate)
                }
            } compactLeading: {
                Image("WidgetLogo")
                    .resizable()
                    .scaledToFit()
                    .clipShape(Circle())
            } compactTrailing: {
                DynamicIslandCompactTimer(targetDate: context.state.targetDate)
            } minimal: {
                DynamicIslandMinimalTimer(targetDate: context.state.targetDate)
            }
        }
    }
}

private struct BreakTimerLockScreenView: View {
    let context: ActivityViewContext<BreakTimerActivityAttributes>

    var isRussian: Bool {
        if #available(iOS 16.0, *) {
            return Locale.current.language.languageCode?.identifier == "ru"
        } else {
            return Locale.current.languageCode == "ru"
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(context.attributes.title)
                .font(.headline)

            Text(isRussian ? "До конца перерыва" : "Break ends in")
                .font(.caption)
                .foregroundStyle(.secondary)

            Text(timerInterval: Date()...context.state.targetDate, countsDown: true)
                .font(.system(size: 34, weight: .semibold, design: .rounded).monospacedDigit())
        }
        .padding()
    }
}

private struct DynamicIslandCompactTimer: View {
    let targetDate: Date

    var body: some View {
        Text(timerInterval: Date()...targetDate, countsDown: true)
            .monospacedDigit()
            .font(.system(size: 14, weight: .semibold))
            .multilineTextAlignment(.trailing)
            .frame(width: 42, alignment: .trailing)
    }
}

private struct DynamicIslandMinimalTimer: View {
    let targetDate: Date

    var body: some View {
        Image("WidgetLogo")
            .resizable()
            .scaledToFit()
            .clipShape(Circle())
    }
}

private struct DynamicIslandExpandedTimer: View {
    let targetDate: Date

    var body: some View {
        HStack(alignment: .bottom) {
            Spacer()
            
            Image("WidgetLogo")
                .resizable()
                .frame(width: 26, height: 26)
                .clipShape(Circle())

            Text(timerInterval: Date()...targetDate, countsDown: true)
                .monospacedDigit()
                .font(.system(size: 14, weight: .semibold))
                .multilineTextAlignment(.trailing)
                .frame(width: 42, alignment: .trailing)
            
            Spacer()
        }
        .padding(.top, 8)
    }
}
