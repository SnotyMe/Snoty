import type { AuthenticationProvider, LoginRequest, RegisterRequest } from "~/utils/auth/auth.ts"
import {
	ResponseError,
	AuthApi,
	type LocalAuthenticationMetadata,
	type LocalAuthRegisterRequest } from "~/api/backend/generated"
import { useApi } from "~/api/backend/api-clients.ts"

const register = async (data: RegisterRequest) => {
	const authApi = useApi(AuthApi)

	const localAuthRegisterRequest: LocalAuthRegisterRequest = {
		email: data.email,
		displayName: data.displayName,
		password: data.password!,
	}

	const { response, value } = await authApi.authLocalRegisterPostRaw({ localAuthRegisterRequest })
		.then(async it => ({ response: it.raw, value: await it.value() }))
		.catch((it: ResponseError) => ({ response: it.response, value: null }))

	if (!response.ok || !value) return response

	useAuth().setCookies({
		accessToken: value.accessToken,
		refreshToken: value.refreshToken,
	})

	return response
}

const login = async (data: LoginRequest) => {
	const authApi = useApi(AuthApi)
	const { response, value } = await authApi.authLocalLoginPostRaw({ localAuthLoginRequest: data })
		.then(async it => ({ response: it.raw, value: await it.value() }))
		.catch((it: ResponseError) => ({ response: it.response, value: null }))

	if (!response.ok || !value) throw new ResponseError(response)

	useAuth().setCookies({
		accessToken: value.accessToken,
		refreshToken: value.refreshToken,
	})
}

const refresh = async () => {
	const authStore = useAuth()
	const refreshToken = authStore.refreshToken
	if (!refreshToken) return

	const authApi = useApi(AuthApi)
	const { response, value } = await authApi.authLocalRefreshPostRaw({ localAuthRefreshRequest: { refreshToken } })
		.then(async it => ({ response: it.raw, value: await it.value() }))
		.catch((it: ResponseError) => ({ response: it.response, value: null }))

	if (!response.ok || !value) throw new ResponseError(response)

	authStore.setCookies({
		accessToken: value.accessToken,
		refreshToken: value.refreshToken,
	})
}

export const localProvider = (metadata: LocalAuthenticationMetadata): AuthenticationProvider => ({
	hasBasicAuth: true,
	hasSelfRegistration: metadata.selfRegistration?.enabled,
	onboardingMode: "instant",
	register,
	login,
	refresh,
})
