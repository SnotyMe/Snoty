import { useApi } from "~/api/backend/api-clients.ts"
import { NotificationApi } from "~/api/backend/generated"

export const useNotifications = () => {
	const notificationApi = useApi(NotificationApi)

	const getCount = () => notificationApi.notificationCountGet()

	const getList = () => notificationApi.notificationListGet()

	const resolve = (notificationId: string) =>
		notificationApi.notificationIdResolvePut({ id: notificationId })

	return {
		getCount,
		getList,
		resolve,
	}
}
