package me.snoty.node.mail

import me.snoty.backend.adapter.AdapterSelector
import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.injection.provideOrThrow
import me.snoty.backend.mail.MailAdapter
import me.snoty.backend.mail.MailService
import org.koin.core.Koin
import org.koin.core.annotation.Single
import org.koin.dsl.module

private const val CONFIG_GROUP = "globalMail"

@Single
fun provideMailNodeService(adapterSelector: AdapterSelector, configLoader: ConfigLoader): MailNodeService = provideOrThrow {
	val adapter = adapterSelector.select(MailAdapter::class, CONFIG_GROUP)
		?: return@provideOrThrow null

	// contains dependencies required by the adapter
	// necessary because we're using a temporary Koin instance to manually load the service using a different adapter context
	val adapterDependenciesModule = module {
		single { configLoader }
	}
	val adapterModule = adapter.provideModule(CONFIG_GROUP)

	val temporaryKoin = Koin()
	temporaryKoin.loadModules(listOf(adapterDependenciesModule, adapterModule))
	val mailService = temporaryKoin.get<MailService>()
	temporaryKoin.close()

	return MailNodeService(mailService)
}

class MailNodeService(mailService: MailService) : MailService by mailService
