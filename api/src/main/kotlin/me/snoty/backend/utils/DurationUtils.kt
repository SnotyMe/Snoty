package me.snoty.backend.utils

import kotlin.time.Duration

/**
 * Same as [toIsoString], but supports days.
 * Necessary to fix https://youtrack.jetbrains.com/issue/KT-40602/Duration.toIsoString-doesnt-strictly-follow-ISO-8601
 */
fun Duration.toProperIsoString(): String = buildString {
	if (isNegative()) append('-')
	append('P')
	absoluteValue.toComponents { days, hours, minutes, seconds, nanoseconds ->
		@Suppress("NAME_SHADOWING")
		var days = days
		if (isInfinite()) {
			// use large enough value instead of Long.MAX_VALUE
			days = 9_999_999_999_999
		}
		val hasDays = days != 0L
		val hasHours = hours != 0
		val hasSeconds = seconds != 0 || nanoseconds != 0
		val hasMinutes = minutes != 0 || (hasSeconds && hasHours)

		if (hasDays) {
			append(days).append('D')
		}

		if (hasHours || hasMinutes || hasSeconds) append('T')

		if (hasHours) append(hours).append('H')
		if (hasMinutes) append(minutes).append('M')
		if (hasSeconds) append(seconds).append('S')
	}
}
