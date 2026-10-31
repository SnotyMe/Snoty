package me.snoty.backend.mail.impl.smtp

import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.config.load
import me.snoty.backend.mail.MailAdapter
import me.snoty.backend.mail.MailService
import org.koin.core.annotation.Single
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

const val SMTP_ADAPTER_TYPE = "smtp"

@Single
class SmtpAdapter : MailAdapter {
	override val supportedTypes = listOf("smtp")

	override fun provideModule(configGroup: String) = module {
		singleOf(::SmtpMailService) bind MailService::class
		single { provideSmtpConfig(get(), configGroup) }
	}

	override val koinModule = provideModule(MailAdapter.CONFIG_GROUP)
}

fun provideSmtpConfig(configLoader: ConfigLoader, configGroup: String): SmtpConfig =
	configLoader.load("$configGroup.${SMTP_ADAPTER_TYPE}")
