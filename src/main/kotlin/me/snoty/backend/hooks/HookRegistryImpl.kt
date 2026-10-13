package me.snoty.backend.hooks

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.snoty.backend.injection.getFromAllScopes
import org.koin.core.Koin
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

@Single
class HookRegistryImpl(
    private val koin: Koin,
) : HookRegistry {
    private val hooks = mutableMapOf<KClass<*>, MutableList<LifecycleHook<*>>>()

    override fun <D : Any, H : LifecycleHook<D>> registerHook(clazz: KClass<H>, hook: H) {
        val list = hooks.getOrPut(clazz) { mutableListOf() }
        list.add(hook)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <D : Any, H : LifecycleHook<D>> executeHooks(clazz: KClass<H>, data: D) = coroutineScope {
        val registeredHooks = hooks[clazz] as List<LifecycleHook<D>>?
        val list = koin.getFromAllScopes(clazz) + (registeredHooks ?: emptyList())
        list.map {
            async {
                it(data)
            }
        }.awaitAll()
        Unit
    }
}
