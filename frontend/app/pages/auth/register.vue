<i18n lang="yaml">
en:
  title: Create an Account
  description: New to Snoty? Create an Account to get started!
  passwordNote: Your Password will be set up after the e-mail verification.
  separator: or
  email:
    label: E-Mail
    placeholder: Your E-Mail Address
  displayName:
    label: Name
    placeholder: Your Username
  password:
    label: Password
    placeholder: Create a password
  confirmPassword:
    label: Confirm Password
    placeholder: Confirm your password
  register: Create Account
  success:
    title: Your Account was created!
    description: Please check your e-mails to finish the onboarding.
    hint: This Tab can be closed.
  hasAccount: Already have an account?
  login: Log in
  registerDisabled: Self Registration is disabled.
  error:
    generic: An unknown error occurred.
    conflict: An Account with this E-Mail Address already exists!
de:
  title: Konto erstellen
  description: Neu bei Snoty? Erstelle ein Konto um zu starten!
  passwordNote: Dein Passwort wird nach der Verifizierung deiner E-Mail-Adresse gesetzt.
  separator: oder
  email:
    label: E-Mail
    placeholder: Deine E-Mail-Adresse
  displayName:
    label: Name
    placeholder: Dein Anzeigename
  password:
    label: Passwort
    placeholder: Erstellen Sie ein Passwort
  confirmPassword:
    label: Passwort bestätigen
    placeholder: Bestätigen Sie Ihr Passwort
  register: Konto erstellen
  success:
    title: Konto erstellt!
    description: Bitte überprüfe deine E-Mails um die Registrierung zu vervollständigen.
    hint: Dieser Tab kann geschlossen werden.
  hasAccount: Bereits registriert?
  login: Anmelden
  registerDisabled: Die Selbstregistrierung ist deaktiviert.
  error:
    generic: Ein unbekannter Fehler ist aufgetreten!
    conflict: Ein Konto mit dieser E-Mail-Adresse existiert bereits!
</i18n>

<script setup lang="ts">
import * as z from "zod"
import type { AuthFormField, FormSubmitEvent } from "@nuxt/ui"
import { definePageMeta } from "#imports"
import { AuthApi, type KeycloakAuthenticationMetadata, type KeycloakRegisterRequest } from "~/api/backend/generated"
import { useApi } from "~/api/backend/api-clients.ts"
import { useAuthProviders } from "~/composables/auth/useAuthProviders.ts"

definePageMeta({
	layout: "center",
})

const { t } = useI18n({ useScope: "local" })

const route = useRoute()
const backlink = route.query.to as string | undefined
const defaultEmail = route.query.email as string | undefined

const fields = computed<AuthFormField[]>(() => [
	{
		name: "email",
		type: "email",
		label: t("email.label"),
		placeholder: t("email.placeholder"),
		defaultValue: defaultEmail,
		required: true,
	},
	{
		name: "displayName",
		type: "text",
		label: t("displayName.label"),
		placeholder: t("displayName.placeholder"),
		required: true,
	},
])

const { authMetadata } = storeToRefs(useAuth())
const providers = useAuthProviders(backlink)

const schema = z.object({
	email: z.email(),
	displayName: z.string(),
})

type Schema = z.output<typeof schema>

const status = ref<"idle" | "loading" | "error" | "success">("idle")
const error = ref()

async function onSubmit(payload: FormSubmitEvent<Schema>) {
	status.value = "loading"
	error.value = undefined
	const keycloakApi = useApi(AuthApi)

	const request: KeycloakRegisterRequest = {
		email: payload.data.email,
		displayName: payload.data.displayName,
	}

	const response = await keycloakApi.authKeycloakRegisterPostRaw({ keycloakRegisterRequest: request })
		.then(it => it.raw)
		.catch(it => it.response)

	if (response.ok) {
		status.value = "success"
	} else {
		status.value = "error"
		error.value = response.status == 409 ? "conflict" : "generic"
	}
}
</script>

<template>
	<AuthContainer>
		<template v-if="status == 'success'">
			<div class="size-full text-center flex flex-col justify-center space-y-1">
				<h1 class="text-xl text-pretty font-semibold text-highlighted">{{ t('success.title') }}</h1>
				<p>{{ t('success.description') }}</p>
				<p class="text-muted">{{ t('success.hint') }}</p>
			</div>
		</template>
		<template v-else>
			<UAuthForm
				v-if="(authMetadata as KeycloakAuthenticationMetadata | undefined)?.onboardingUrl"
				:schema="schema"
				:title="t('title')"
				:description="t('description')"
				:separator="t('separator')"
				:providers="providers"
				:fields="fields"
				:submit="{ label: t('register') }"
				:loading-auto="false"
				:loading="status == 'loading'"
				@submit="onSubmit"
			>
				<template #validation>
					<UFormField :help="t('passwordNote')"/>
				</template>
			</UAuthForm>
			<template v-else>
				<p>{{ t('registerDisabled') }}</p>
			</template>
			<UAlert
				v-if="error"
				:title="t(`error.${error}`)"
				color="error"
				variant="subtle"
				size="sm"
				class="mt-2"
			/>
			<div class="absolute mt-4 text-center text-sm bottom-0 right-0">
				{{ t("hasAccount") }}
				<NuxtLink
					:to="buildUrl('/auth/login', { to: backlink })"
					class="ml-1 font-medium text-primary"
				>
					{{ t("login") }}
				</NuxtLink>
			</div>
		</template>
	</AuthContainer>
</template>
