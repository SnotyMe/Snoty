<script setup lang="ts">
import { useNotifications } from "~/composables/notification/useNotifications.ts"
import type { TabsItem } from "@nuxt/ui"
import LoadingTexts from "~/components/ui/LoadingTexts.vue"

const { getList } = useNotifications()
const { data: notifications, pending: loading } = useAsyncData(getList, { deep: true, lazy: true })

const tabs = computed<TabsItem[]>(() => [
	{
		label: "All",
		value: "all",
	},
	{
		label: "Unread",
		value: "unread",
	},
])
const readFilter = ref("all")

const filteredNotifications = computed(() => {
	const allNotifications = notifications.value
	if (!allNotifications) return []

	return allNotifications.filter(it =>
		readFilter.value == "all" || it.resolvedAt == null,
	)
})
</script>

<template>
	<UDashboardPanel id="notifications">
		<template #header>
			<UDashboardNavbar :title="$t('navigation.page.notifications')">
				<template #right>
					<UTabs
						v-model="readFilter"
						:items="tabs"
						size="sm"
						:content="false"
					/>
				</template>
			</UDashboardNavbar>
		</template>

		<template #body>
			<template v-if="loading">
				<UProgress size="xs"/>
				<LoadingTexts class="text-center"/>
			</template>
			<NotificationTable v-else v-model="filteredNotifications" :loading="loading"/>
		</template>
	</UDashboardPanel>
</template>
