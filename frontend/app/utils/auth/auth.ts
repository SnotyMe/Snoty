import type {
	AuthenticationMetadata,
	KeycloakAuthenticationMetadata,
	LocalAuthenticationMetadata,
	OidcAuthenticationMetadata,
} from "~/api/backend/generated"
import type { ButtonProps } from "@nuxt/ui"
import { keycloakProvider } from "~/utils/auth/keycloak.ts"
import { localProvider } from "~/utils/auth/local.ts"
import { oidcProvider } from "~/utils/auth/oidc.ts"

export interface RegisterRequest {
	email: string
	displayName: string
	password?: string
}

export interface LoginRequest {
	email: string
	password: string
}

export interface AuthenticationProvider {
	hasBasicAuth?: boolean
	hasSelfRegistration?: boolean
	onboardingMode?: "instant" | "verify-email"

	getProviders?: (backlink: string | undefined) => ButtonProps[]
	getRedirectUrl?: (backlink: string | undefined) => string

	register?: (data: RegisterRequest) => Promise<Response>
	login: (data: LoginRequest) => Promise<void>
	refresh?: () => Promise<void>
	logout?: () => Promise<void>
}

export const getAuthenticationProvider = <A extends AuthenticationMetadata>(metadata: A): AuthenticationProvider => {
	switch (metadata.adapter) {
		case "local":
			return localProvider(metadata as LocalAuthenticationMetadata)
		case "keycloak":
			return keycloakProvider(metadata as KeycloakAuthenticationMetadata)
		case "oidc":
			return oidcProvider(metadata as OidcAuthenticationMetadata)
		default:
			throw new Error(`Unsupported authentication adapter: ${metadata.adapter}`)
	}
}
