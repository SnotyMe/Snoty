package me.snoty.backend.adapter

class NoAdapterSelectedException(
	configKey: String,
) : RuntimeException("No $configKey adapter selected!")

class MultipleAdaptersSelectedException(
	configKey: String,
	potential: List<Adapter>,
) : RuntimeException("Multiple $configKey adapters selected! ($potential)")
