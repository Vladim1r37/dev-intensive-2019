package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val interval = date.time - this.time

    return when (interval.absoluteValue) {
        in 1..1000 -> "только что"
        in 1001..45000 -> if (interval > 0) "несколько секунд назад" else "через несколько секунд"
        in 45001..75000 -> if (interval > 0) "минуту назад" else "через несколько минут"
        in 75001..2700000 -> if (interval > 0) {
            "${getVerboseInterval(interval.absoluteValue, TimeUnits.MINUTE)} назад"
        } else {
            "через ${getVerboseInterval(interval.absoluteValue, TimeUnits.MINUTE)}"
        }
        in 2700001..4500000 -> if (interval > 0) "час назад" else "через час"
        in 4500001..79200000 -> if (interval > 0) {
            "${getVerboseInterval(interval.absoluteValue, TimeUnits.HOUR)} назад"
        } else {
            "через ${getVerboseInterval(interval.absoluteValue, TimeUnits.HOUR)}"
        }
        in 79200001..93600000 -> if (interval > 0) "день назад" else "через день"
        in 93600001..31104000000 -> if (interval > 0) {
            "${getVerboseInterval(interval.absoluteValue, TimeUnits.DAY)} назад"
        } else {
            "через ${getVerboseInterval(interval.absoluteValue, TimeUnits.DAY)}"
        }
        else -> if (interval > 0) "более года назад" else "более чем через год"
    }
}

fun getVerboseInterval(interval: Long, units: TimeUnits): String {
    val unitValue = when (units) {
        TimeUnits.SECOND -> SECOND
        TimeUnits.MINUTE -> MINUTE
        TimeUnits.HOUR -> HOUR
        TimeUnits.DAY -> DAY
    }
    val number = Math.ceil(interval.toDouble() / unitValue).toInt()

    val unitName = when (units) {
        TimeUnits.SECOND -> when (number) {
            1 -> "секунда"
            in 2..4 -> "секунды"
            else -> "секунд"
        }
        TimeUnits.MINUTE -> when (number) {
            1 -> "минута"
            in 2..4 -> "минуты"
            else -> "минут"
        }
        TimeUnits.HOUR -> when (number) {
            1 -> "час"
            21 -> "час"
            in 2..4 -> "часа"
            22 -> "часа"
            else -> "часов"
        }
        TimeUnits.DAY -> when (number) {
            1 -> "день"
            in 2..4 -> "дня"
            in 5..20 -> "дней"
            else -> when (number%10) {
                1 -> "день"
                in 2..4 -> "дня"
                else -> "дней"
            }
        }
    }

    return "$number $unitName"
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}
