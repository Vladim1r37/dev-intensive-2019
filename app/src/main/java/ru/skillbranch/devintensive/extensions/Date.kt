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
    var prefix = ""
    var postfix = ""
    if (interval > 0) postfix = " назад" else prefix = "через "

    return when (interval.absoluteValue) {
        in 0..1 * SECOND -> "только что"
        in 1 * SECOND..45 * SECOND -> prefix + "несколько секунд$postfix"
        in 45 * SECOND..75 * SECOND -> prefix + "минуту$postfix"
        in 75 * SECOND..45 * MINUTE -> prefix + "${getVerboseInterval(interval, TimeUnits.MINUTE)}$postfix"
        in 45 * MINUTE..75 * MINUTE -> prefix + "час$postfix"
        in 75 * MINUTE..22 * HOUR -> prefix + "${getVerboseInterval(interval, TimeUnits.HOUR)}$postfix"
        in 22 * HOUR..26 * HOUR -> prefix + "день$postfix"
        in 26 * HOUR..360 * DAY -> prefix + "${getVerboseInterval(interval, TimeUnits.DAY)}$postfix"
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
    val number = if (interval < 0) Math.ceil(interval.absoluteValue.toDouble() / unitValue).toInt()
    else Math.floor(interval.toDouble() / unitValue).toInt()


    return units.plural(number)
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(num: Int): String {
        return "$num ${
        when (this) {
            SECOND -> {
                val declensions = arrayOf("секунду", "секунды", "секунд")
                declensions[getDescIndex(num)]
            }
            MINUTE -> {
                val declensions = arrayOf("минуту", "минуты", "минут")
                declensions[getDescIndex(num)]
            }
            HOUR -> {
                val declensions = arrayOf("час", "часа", "часов")
                declensions[getDescIndex(num)]
            }
            DAY -> {
                val declensions = arrayOf("день", "дня", "дней")
                declensions[getDescIndex(num)]
            }
        }
        }"
    }

    private fun getDescIndex(num: Int): Int {
        return if (num in 11..14) 2 else when (num % 10) {
            1 -> 0
            in 2..4 -> 1
            else -> 2
        }
    }
}
