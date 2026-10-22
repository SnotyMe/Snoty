import { useNotifications } from "~/composables/notification/useNotifications.ts"

export const useNotificationStore = defineStore("notifications", () => {
	const notificationsComposable = useNotifications()

	const { state: count, executeImmediate: refreshCount } = useAsyncState(
		() => notificationsComposable.getCount(),
		null, { immediate: true, resetOnExecute: false },
	)

	return {
		count,
		refreshCount,
	}
})
