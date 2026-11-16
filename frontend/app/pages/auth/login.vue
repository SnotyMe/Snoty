<i18n lang="yaml">
en:
  description: Enter your details to log in to Snoty.
  login: Login
  separator: or
  email:
    label: E-Mail
    placeholder: Your E-Mail Address
  password:
    label: Password
    placeholder: Enter your password
  noAccount: New to Snoty?
  register: Sign up
  error:
    generic: Error signing in.
    invalidCredentials: Invalid credentials.
de:
  description: Gib deine Daten ein, um dich in Snoty einzuloggen.
  login: Anmelden
  separator: oder
  email:
    label: E-Mail
    placeholder: Deine E-Mail-Adresse
  password:
    label: Passwort
    placeholder: Geben Sie Ihr Passwort ein
  noAccount: Neu bei Snoty?
  register: Registrieren
  error:
    generic: Fehler beim Login.
    invalidCredentials: Ungültige Zugangsdaten.
</i18n>

<script setup lang="ts">
import * as z from "zod"
import type { AuthFormField, FormSubmitEvent } from "@nuxt/ui"
import { definePageMeta } from "#imports"
import { useAuth } from "~/stores/auth"
import { ResponseError } from "~/api/backend/generated"
import { buildUrl } from "~/utils/url.ts"

definePageMeta({
	layout: "center",
})

const { t } = useI18n({ useScope: "local" })

const fields: AuthFormField[] = [
	{
		name: "email",
		type: "email",
		label: t("email.label"),
		placeholder: t("email.placeholder"),
		required: true,
	},
	{
		name: "password",
		type: "password",
		label: t("password.label"),
		placeholder: t("password.placeholder"),
		required: true,
	},
]

const authStore = useAuth()
const { authProvider } = storeToRefs(authStore)

const route = useRoute()
const backlink = route.query.to as string | undefined

const [
	DefineNoAccount,
	ReuseNoAccount,
] = createReusableTemplate()

const schema = z.object({
	email: z.string("E-Mail is required"),
	password: z.string("Password is required"),
})

type Schema = z.output<typeof schema>

const authForm = useTemplateRef("authForm")

const error = ref<string>()

async function onSubmit(payload: FormSubmitEvent<Schema>) {
	error.value = undefined
	try {
		await authStore.login({
			email: payload.data.email,
			password: payload.data.password,
		})
	} catch (e) {
		if (e instanceof ResponseError && e.response.status == 400) {
			error.value = "invalidCredentials"
		} else {
			error.value = "generic"
		}
	}
	await authStore.safeNavigateTo(backlink)
}
</script>

<template>
	<DefineNoAccount>
		{{ t("noAccount") }}
		<NuxtLink
			:to="buildUrl('/auth/register', {
				to: backlink,
				email: authForm?.state?.email,
			})"
			class="ml-1 font-medium text-primary"
		>
			{{ t("register") }}
		</NuxtLink>
	</DefineNoAccount>

	<AuthContainer>
		<UAuthForm
			ref="authForm"
			:schema="schema"
			title="Login"
			:description="t('description')"
			:separator="t('separator')"
			:fields="authProvider?.hasBasicAuth ? fields : undefined"
			:providers="authProvider?.getProviders?.(backlink)"
			@submit="onSubmit"
		/>
		<UAlert
			v-if="error"
			:title="t(`error.${error}`)"
			color="error"
			variant="subtle"
			size="sm"
			class="mt-2"
		>
			<template #title>
				{{ t(`error.${error}`) }} <ReuseNoAccount/>
			</template>
		</UAlert>
		<div v-if="authProvider?.hasSelfRegistration" class="absolute mt-4 text-center text-sm bottom-0 right-0">
			<ReuseNoAccount/>
		</div>
	</AuthContainer>
</template>
