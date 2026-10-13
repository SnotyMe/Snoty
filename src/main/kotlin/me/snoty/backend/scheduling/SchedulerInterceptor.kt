package me.snoty.backend.scheduling

import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class SchedulerInterceptor(
    @Named("adapter") private val delegate: Scheduler,
) : Scheduler by delegate
