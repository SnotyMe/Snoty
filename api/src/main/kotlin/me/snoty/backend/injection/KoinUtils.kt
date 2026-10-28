package me.snoty.backend.injection

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.NoDefinitionFoundException
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Koin.getFromAllScopes(): List<T> =
	scopeRegistry.scopeDefinitions.map {
		getOrCreateScope(it.value, it)
	}
		.flatMap { it.getAll<T>() }
		.distinct()

@OptIn(KoinInternalApi::class)
fun <T : Any> Koin.getFromAllScopes(clazz: KClass<T>): List<T> =
	scopeRegistry.scopeDefinitions.map {
		getOrCreateScope(it.value, it)
	}
		.flatMap { it.getAll<T>(clazz) }
		.distinct()

inline fun <reified T : Any> provideOrThrow(provide: () -> T?): T =
	provide()
		?: throw NoDefinitionFoundException("Could not provide '${T::class.qualifiedName}'")
