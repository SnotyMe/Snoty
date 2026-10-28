package me.snoty.backend.mail

import me.snoty.backend.adapter.Adapter
import org.koin.core.module.Module

interface MailAdapter : Adapter {
	companion object {
		const val CONFIG_GROUP = "mail"
	}

	fun provideModule(configGroup: String): Module
}
