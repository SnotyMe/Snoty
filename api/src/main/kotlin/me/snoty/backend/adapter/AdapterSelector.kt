package me.snoty.backend.adapter

import kotlin.reflect.KClass

interface AdapterSelector {
	fun <T : Adapter> select(adapterClass: KClass<T>, configKey: String): T?
	fun <T : Adapter> load(adapterClass: KClass<T>, configKey: String): T
	fun <T : Adapter> loadOptional(adapterClass: KClass<T>, configKey: String): T?
}
