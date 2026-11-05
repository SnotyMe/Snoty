<script setup lang="ts">
defineProps<{
	title: string
	icon: string
}>()
</script>

<template>
	<span class="space-x-0.5" :title="title">
		<UIcon :name="icon" class="align-middle mr-1"/>
		<i>
			{{ title }}:
		</i>
		<span><slot/></span>
	</span>
</template>
