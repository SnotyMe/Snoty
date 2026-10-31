<script setup lang="ts">
import BasicSettings from "~/components/settings/BasicSettings.vue"

useHead({
	title: "Settings",
})
</script>

<template>
	<UForm
		id="settings"
	>
		<UPageCard variant="subtle">
			<BasicSettings/>
		</UPageCard>
	</UForm>
</template>
