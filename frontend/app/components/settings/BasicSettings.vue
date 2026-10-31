<script setup lang="ts">
import { useUserSettingsStore } from "~/stores/userSettings"
import AppLocaleSelector from "#layers/base/app/components/framework/AppLocaleSelector.vue"

const settings = useUserSettingsStore()
</script>

<template>
	<UFormField
		name="language"
		:label="$t('settings.language')"
		class="flex max-xs:flex-col justify-between items-center gap-4"
	>
		<AppLocaleSelector/>
	</UFormField>
	<USeparator/>
	<UFormField
		name="dateFormat"
		:label="$t('settings.dateFormat')"
		class="flex max-xs:flex-col justify-between items-center gap-4"
	>
		<USelect
			v-model="settings.dateFormat"
			:items="[
				...Object.values(settings.dateFormatDefault),
			]"
		/>
	</UFormField>
	<USeparator/>
	<UFormField
		name="colorScheme"
		:label="$t('settings.colorScheme')"
		class="flex max-xs:flex-col justify-between items-center gap-4"
	>
		<UColorModeSelect/>
	</UFormField>
</template>
