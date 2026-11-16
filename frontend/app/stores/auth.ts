import { useApi } from "~/api/backend/api-clients"
import { AuthApi, type AuthenticationMetadata, type Role } from "~/api/backend/generated"
import { jwtDecode } from "jwt-decode"
import type { User } from "#layers/base/app/types/user.ts"
import type { AuthComposable } from "#layers/base/app/types/auth.ts"
import { getAuthenticationProvider, type LoginRequest, type RegisterRequest } from "~/utils/auth/auth.ts"
import { ifDefined } from "~/utils/utils.ts"

export const useAuth = defineStore("auth", () => {
	const authApi = useApi(AuthApi)

	const thirtyDaysInSeconds = 60 * 60 * 24 * 30

	const accessTokenCookie = useCookie("access_token", { maxAge: thirtyDaysInSeconds })
	const refreshTokenCookie = useCookie("refresh_token", { maxAge: thirtyDaysInSeconds })
	const idTokenCookie = useCookie("id_token", { maxAge: thirtyDaysInSeconds })

	const getAuthMetadata = async (): Promise<AuthenticationMetadata> =>
		authApi.authMetadataGet({
			credentials: "omit",
		})

	const { data: authMetadata } = useAsyncData("authMetadata", getAuthMetadata)
	const authProvider = computed(() => ifDefined(authMetadata.value, getAuthenticationProvider))

	const setCookies = (data: { accessToken: string, refreshToken?: string, idToken?: string }) => {
		accessTokenCookie.value = data.accessToken
		refreshTokenCookie.value = data.refreshToken
		idTokenCookie.value = data.idToken
	}

	const safeNavigateTo = (backlink: string | undefined) => {
		try {
			return navigateTo(backlink ?? "/")
		} catch {
			// most likely error: Nuxt's protection against open redirects => default to /
			return navigateTo("/")
		}
	}

	const register = async (data: RegisterRequest) =>
		await authProvider.value?.register?.(data)

	const login = async (data: LoginRequest) =>
		await authProvider.value?.login(data)

	const refresh = async () =>
		authProvider.value?.refresh?.()

	const logout = async () => {
		await authProvider.value?.logout?.()

		accessTokenCookie.value = undefined
		refreshTokenCookie.value = undefined
		idTokenCookie.value = undefined

		return navigateTo("/auth/login")
	}

	const jwt = computed(() => {
		if (!accessTokenCookie.value) return undefined

		return jwtDecode(accessTokenCookie.value)
	})

	const user = computed(() => {
		// eslint-disable-next-line
		const jwtValue = jwt.value as any | undefined
		if (!jwtValue) return undefined

		return {
			id: jwtValue.sub,
			email: jwtValue.email,
			name: jwtValue.name,
			firstName: jwtValue.given_name,
			lastName: jwtValue.family_name,
			displayName: jwtValue.displayName,
		} as User
	})

	const hasRole = async (role: string) => {
		const roles = await authApi.authRolesGet() as never as Role[]

		return roles.some(r => r.name === role)
	}

	return {
		authMetadata,
		authProvider,
		setCookies,
		register,
		login,
		refresh,
		logout,
		user,
		hasRole,
		safeNavigateTo,
		accessToken: accessTokenCookie,
		refreshToken: refreshTokenCookie,
		idToken: idTokenCookie,
	}
}) satisfies AuthComposable
