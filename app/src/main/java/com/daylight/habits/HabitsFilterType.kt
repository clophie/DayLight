package com.daylight.habits


/**
 * Used with the filter spinner in the habits list.
 */
enum class HabitsFilterType {
    /**
     * Do not filter habits.
     */
    ALL_HABITS,

    /**
     * Filters only the active (not completed yet) habits.
     */
    ACTIVE_HABITS,

    /**
     * Filters only the completed habits.
     */
    COMPLETED_HABITS
}
