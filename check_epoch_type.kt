
import kotlinx.datetime.LocalDate

fun main() {
    val date = LocalDate(2023, 1, 1)
    val epochDays = date.toEpochDays()
    println(epochDays::class.simpleName)
}
