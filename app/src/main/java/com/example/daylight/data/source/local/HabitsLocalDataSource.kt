package com.example.daylight.data.source.local

import com.example.daylight.data.source.HabitsDataSource

/**
 * Concrete implementation of a data source as a db.
 */
class HabitsLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val habitsDao: HabitsDao
) : HabitsDataSource {

}