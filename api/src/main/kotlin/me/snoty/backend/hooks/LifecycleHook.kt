package me.snoty.backend.hooks

fun interface LifecycleHook<in T : Any> {
    suspend operator fun invoke(data: T)
}
