package me.snoty.backend.wiring.node

import me.snoty.backend.hooks.LifecycleHook
import me.snoty.core.node.NodeWithSettings

fun interface NodeCreatedHook : LifecycleHook<NodeWithSettings>

fun interface NodeUpdatedHook : LifecycleHook<NodeWithSettings>

fun interface NodeDeletedHook : LifecycleHook<NodeWithSettings>
