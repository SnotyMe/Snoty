<script setup lang="ts">
import type { Notification } from "~/api/backend/generated"
import type { TableColumn } from "@nuxt/ui"
import DateTimeFormat from "~/components/primitives/DateTimeFormat.vue"
import { useNotifications } from "~/composables/notification/useNotifications.ts"
import { useNotificationStore } from "~/stores/notifications.ts"
import NotificationTableDetailsCell from "~/components/notification/NotificationTableDetailsCell.vue"

const notificationStore = useNotificationStore()
const { resolve } = useNotifications()

const notifications = defineModel<Notification[] | undefined>({ required: true })

defineProps<{
	loading: boolean
}>()

const columns = computed<TableColumn<Notification>[]>(() => [
	{
		id: "actions",
		header: "Actions",
		meta: {
			class: {
				td: "w-px pr-0 align-middle",
			},
		},
	},
	{
		id: "details",
		header: "Details",
		meta: {
			class: {
				td: "max-w-sm lg:max-w-lg",
			},
		},
	},
	{
		id: "lastSeenAt",
		header: "Last Seen",
		meta: {
			class: {
				td: "text-right",
			},
		},
	},
])

const resolveNotification = async (notification: Notification) => {
	await resolve(notification.id)

	notification.resolvedAt = new Date()

	void notificationStore.refreshCount()
}
</script>

<template>
	<UTable
		:columns="columns"
		:data="notifications"
		:ui="{ thead: 'hidden', tr: 'hover:bg-muted' }"
	>
		<template #actions-cell="{ row: { original: notification } }">
			<UButton
				:icon="notification.resolvedAt != null ? 'i-lucide-bell' : 'i-lucide-bell-dot'"
				:color="notification.resolvedAt != null ? 'neutral' : 'primary'"
				:variant="notification.resolvedAt != null ? 'link' : 'subtle'"
				:disabled="notification.resolvedAt != null"
				class="place-self-center"
				loading-auto
				@click="resolveNotification(notification)"
			/>
		</template>
		<template #details-cell="{ row }">
			<NotificationTableDetailsCell :notification="row.original"/>
		</template>

		<template #lastSeenAt-cell="{ row: { original: notification } }">
			<div class="flex flex-1 flex-col justify-between">
				<p class="text-md text-default"><DateTimeFormat :date="notification.lastSeenAt"/></p>
				<p class="text-sm">occurred {{ notification.count }}x</p>
			</div>
		</template>
	</UTable>
</template>
