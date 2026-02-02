package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

data class DailyBreakStats(
    val totalBreaks: Int = 0,
    val completedBreaks: Int = 0
) {
    val completionPercentage: Float
        get() = if (totalBreaks > 0) completedBreaks.toFloat() / totalBreaks else 0f
}

class StatisticsViewModel(
    database: TaskDatabase
) : ViewModel() {
    private val dao = database.taskDao()
    
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyStats: StateFlow<DailyBreakStats> = _selectedDate
        .flatMapLatest { date ->
            if (date != null) {
                dao.getBreaksByDate(date.toString())
                    .map { breaks ->
                        val restBreaks = breaks.filter { it.isForBreak }
                        DailyBreakStats(
                            totalBreaks = restBreaks.size,
                            completedBreaks = restBreaks.count { it.isCompleted }
                        )
                    }
            } else {
                flowOf(DailyBreakStats())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyBreakStats())
}
