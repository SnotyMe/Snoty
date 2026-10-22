<script setup lang="ts">
import type { Notification } from "~/api/backend/generated"

defineProps<{
	notification: Notification
}>()
</script>

<template>
	<div class="flex gap-1">
		<UButton
			v-if="notification.attributes['node.id']"
			label="Node"
			trailing-icon="i-lucide-arrow-right"
			size="xs"
			color="neutral"
			variant="subtle"
			:to="`/flows/${notification.attributes['flow.id']}?highlightedNode=${notification.attributes['node.id']}`"
		/>
	</div>
</template>
