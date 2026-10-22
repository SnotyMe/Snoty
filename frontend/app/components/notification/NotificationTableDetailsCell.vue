<script setup lang="ts">
import type { Notification } from "~/api/backend/generated"

defineProps<{
	notification: Notification
}>()

const expanded = ref(false)

const descriptionElement = useTemplateRef("description")

const canExpand = computed(() => {
	const descriptionElementValue = descriptionElement.value
	if (!descriptionElementValue) return false

	return descriptionElementValue.offsetWidth < descriptionElementValue.scrollWidth
})
</script>

<template>
	<div class="flex items-center gap-2">
		<NotificationActions :notification="notification"/>

		<h5 class="overflow-x-hidden text-ellipsis text-default text-bold text-lg" :class="{ 'text-highlighted': notification.resolvedAt == null }">
			{{ notification.title }}
		</h5>
	</div>
	<p
		ref="description"
		class="overflow-x-hidden"
		:class="{
			'text-default': notification.resolvedAt == null,
			'text-wrap cursor-n-resize': canExpand && expanded,
			'cursor-s-resize': canExpand && !expanded,
			'text-ellipsis': !expanded,
		}"
		@click="expanded = !expanded"
	>
		{{ notification.description }}
	</p>
</template>
