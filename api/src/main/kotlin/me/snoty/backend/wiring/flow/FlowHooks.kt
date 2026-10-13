package me.snoty.backend.wiring.flow

import me.snoty.backend.hooks.LifecycleHook
import me.snoty.core.flow.Workflow

fun interface FlowCreatedHook : LifecycleHook<Workflow>

fun interface FlowUpdatedHook : LifecycleHook<Workflow>

fun interface FlowDeletedHook : LifecycleHook<Workflow>
