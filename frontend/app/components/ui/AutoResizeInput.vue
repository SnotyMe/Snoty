<script setup lang="ts">
const model = defineModel<string | number>()
</script>

<template>
	<div class="inline-grid items-center min-w-20">
		<span
			class="invisible col-start-1 row-start-1 whitespace-pre text-sm px-2.5 pointer-events-none select-none"
			aria-hidden="true"
		>{{ model }}</span>
		<UInput
			v-model="model"
			v-bind="$attrs"
			class="col-start-1 row-start-1 w-full min-w-0"
			:ui="{ root: 'w-full min-w-0', base: 'w-full min-w-0' }"
		/>
	</div>
</template>
