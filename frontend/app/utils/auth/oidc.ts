import type { AuthenticationProvider } from "~/utils/auth/auth.ts"
import type { OAuth2TokenResponse, OidcAuthenticationMetadata } from "~/api/backend/generated"

export const tokenResponseToCookies = (data: OAuth2TokenResponse) => {
	const authStore = useAuth()
	authStore.setCookies({
		accessToken: data.access_token,
		refreshToken: data.refresh_token ?? undefined,
		idToken: data.id_token ?? undefined,
	})
}

export const oidcProvider = (metadata: OidcAuthenticationMetadata): AuthenticationProvider => ({
	hasBasicAuth: metadata.publicClientId != undefined,
	login: () => {
		throw Error("OIDC Login is not supported yet.")
	},
})
