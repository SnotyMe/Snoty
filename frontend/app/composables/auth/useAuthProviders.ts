import { useAuth } from "~/stores/auth.ts"
import type { KeycloakAuthenticationMetadata } from "~/api/backend/generated"

export const useAuthProviders = (backlink: string | undefined) => {
	const { authMetadata } = storeToRefs(useAuth())

	return computed(() => {
		const metadata = authMetadata.value
		if (!metadata) return

		switch (metadata?.adapter) {
			case "keycloak":
				return getKeycloakProviders(metadata as KeycloakAuthenticationMetadata, backlink)
			default:
				return []
		}
	})
}
