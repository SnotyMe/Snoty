<script setup lang="ts">
const query = defineModel<string>({ required: true })

defineProps<{
	placeholder: string
}>()

const queryInput = useTemplateRef("queryInput")
defineShortcuts({
	"/": () => {
		queryInput.value?.inputRef?.focus()
	},
})
</script>

<template>
	<UInput
		ref="queryInput"
		v-model="query"
		icon="i-lucide-search"
		type="text"
		:placeholder="placeholder"
		:ui="{ trailing: 'pe-1' }"
	>
		<template #trailing>
			<UButton
				v-if="query?.length"
				color="neutral"
				variant="link"
				size="sm"
				icon="i-lucide-circle-x"
				@click="query = ''"
			/>
			<UKbd value="/"/>
		</template>
	</UInput>
</template>
