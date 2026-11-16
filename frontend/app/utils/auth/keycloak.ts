import {
	AuthApi,
	type KeycloakAuthenticationMetadata,
	type OAuth2TokenResponse,
	ResponseError,
} from "~/api/backend/generated"
import type { AuthenticationProvider, LoginRequest, RegisterRequest } from "~/utils/auth/auth.ts"
import { useApi } from "~/api/backend/api-clients.ts"
import type { KeycloakRegisterRequest } from "~/api/backend/generated/models/KeycloakRegisterRequest.ts"
import { useAuth } from "#imports"
import { tokenResponseToCookies } from "~/utils/auth/oidc.ts"

const getRedirectUrl = (backlink: string | undefined) => {
	const url = useRequestURL()
	return `${url.protocol}//${url.host}/auth/callback${backlink ? `?to=${encodeURIComponent(backlink)}` : ""}`
}

const getProviders = (
	metadata: KeycloakAuthenticationMetadata,
	backlink: string | undefined,
) => {
	const authUrl = new URL(metadata.authUrl)
	authUrl.searchParams.append("client_id", metadata.clientId)
	authUrl.searchParams.append("response_type", "code")
	authUrl.searchParams.append("scope", "openid")
	authUrl.searchParams.append("redirect_uri", getRedirectUrl(backlink))

	return metadata.providers
		.map(provider => ({
			label: provider.displayName ?? provider.alias,
			icon: `i-simple-icons-${provider.providerId}`,
			to: `${authUrl}&kc_idp_hint=${provider.alias}`,
		}))
}

const register = async (data: RegisterRequest) => {
	const keycloakApi = useApi(AuthApi)

	const request: KeycloakRegisterRequest = {
		email: data.email,
		displayName: data.displayName,
	}

	return await keycloakApi.authKeycloakRegisterPostRaw({ keycloakRegisterRequest: request })
		.then(it => it.raw)
		.catch(it => it.response)
}

const loginBasic = async (metadata: KeycloakAuthenticationMetadata, request: LoginRequest) => {
	const publicClientId = metadata.publicClientId
	if (!publicClientId) {
		throw new Error("Cannot login as the Keycloak public client ID is not configured")
	}
	const params = new URLSearchParams({
		client_id: publicClientId,
		username: request.email,
		password: request.password,
		scope: "openid",
		grant_type: "password",
	})
	const response = await fetch(
		metadata.tokenUrl,
		{
			method: "POST",
			body: params,
		},
	)

	if (!response.ok) {
		throw new ResponseError(response)
	}

	const json: OAuth2TokenResponse = await response.json()
	tokenResponseToCookies(json)
}

const refresh = async () => {
	const authStore = useAuth()
	if (!authStore.refreshToken) return

	const authApi = useApi(AuthApi)

	const response = await authApi.authRefreshPost({ refreshToken: authStore.refreshToken })

	tokenResponseToCookies(response)
}

const logout = async () => {
	const authStore = useAuth()
	const authApi = useApi(AuthApi)
	const idToken = authStore.idToken
	if (!idToken) {
		console.error("Cannot logout from Keycloak as the ID token is not available")
		return
	}
	await authApi.authLogoutPost({ idToken })
}

export const keycloakProvider = (metadata: KeycloakAuthenticationMetadata): AuthenticationProvider => ({
	hasBasicAuth: metadata.publicClientId != undefined,
	hasSelfRegistration: metadata.onboardingUrl != undefined,
	onboardingMode: metadata.onboardingUrl ? "verify-email" : undefined,
	getProviders: backlink => getProviders(metadata, backlink),
	getRedirectUrl,
	register,
	login: (request: LoginRequest) => loginBasic(metadata, request),
	refresh,
	logout,
})
