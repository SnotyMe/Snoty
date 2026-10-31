<i18n lang="yaml">
en:
  password:
    label: Password
    placeholder: Enter your password
  passwordConfirm:
    label: Confirm Password
    placeholder: Enter the same password again
  finish: Finish Onboarding
de:
  password:
    label: Passwort
    placeholder: Dein Passwort
  passwordConfirm:
    label: Passwort (Bestätigung)
    placeholder: Das selbe Passwort noch einmal
  finish: Onboarding abschließen
</i18n>

<script setup lang="ts">
import type { AuthFormField, FormSubmitEvent, StepperItem } from "@nuxt/ui"
import BasicSettings from "~/components/settings/BasicSettings.vue"
import { useAuth } from "~/stores/auth.ts"
import type { KeycloakAuthenticationMetadata } from "~/api/backend/generated"
import { z } from "zod"
import { jwtDecode } from "jwt-decode"

const authStore = useAuth()
const { authMetadata } = storeToRefs(authStore)

const route = useRoute()
const token = route.query.token as string
interface OnboardingToken {
	clientId: string
	email: string
	displayName: string
}
const { clientId, email, displayName } = jwtDecode<OnboardingToken>(token)

definePageMeta({
	layout: "center",
})

const { t } = useI18n({ useScope: "local" })

const authenticationStatus = ref<"idle" | "loading" | "error" | "success">("idle")

const stepperItems = computed<StepperItem[]>(() => {
	const authStatus = authenticationStatus.value
	return [
		{
			slot: "auth",
			title: "Authentication",
			disabled: authStatus == "success",
			ui: {
				trigger: authStatus == "error" && "bg-error!",
				icon: authStatus == "loading" && "animate-spin",
			},
			icon: authStatus == "idle"
				? "i-lucide-key"
				: (authStatus == "loading"
						? "i-lucide-loader-2"
						: (authStatus == "error" ? "i-lucide-circle-x" : "i-lucide-check")),
		},
		{
			slot: "personalization",
			title: "Personalization",
			icon: "i-lucide-palette",
		},
	]
})

const fields = computed<AuthFormField[]>(() => [
	{
		name: "password",
		type: "password",
		label: t("password.label"),
		placeholder: t("password.placeholder"),
		required: true,
	},
	{
		name: "passwordConfirm",
		type: "password",
		label: t("passwordConfirm.label"),
		placeholder: t("passwordConfirm.placeholder"),
		required: true,
	},
])

const stepper = useTemplateRef("stepper")

const schema = z.object({
	password: z.string(),
	passwordConfirm: z.string(),
}).refine(data => data.password === data.passwordConfirm, {
	message: "Passwords do not match",
	path: ["passwordConfirm"],
})

type Schema = z.output<typeof schema>

const confirmAuthentication = async (payload: FormSubmitEvent<Schema>) => {
	stepper.value?.next()

	const metadata = authMetadata.value as KeycloakAuthenticationMetadata | undefined
	if (metadata == undefined || metadata.onboardingUrl == undefined) {
		console.error("Metadata is null or Onboarding not supported")
		authenticationStatus.value = "error"
		return
	}

	authenticationStatus.value = "loading"

	try {
		const response = await fetch(metadata.onboardingUrl, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				token,
				clientId,
				password: payload.data.password,
			}),
		})
		if (!response.ok) throw Error("Response was not ok")

		await authStore.loginBasic(email, payload.data.password)
	} catch {
		authenticationStatus.value = "error"
		return
	}

	authenticationStatus.value = "success"
}

const finish = () => {
	navigateTo("/")
}
</script>

<template>
	<AuthContainer :ui="{ wrapper: 'space-y-4' }">
		<div class="text-start flex items-center justify-center gap-3">
			<UAvatar :alt="displayName" size="xl"/>
			<div>
				<p class="font-medium text-highlighted">{{ displayName }}</p>
				<p class="text-sm text-muted">{{ email }}</p>
			</div>
		</div>

		<UStepper
			ref="stepper"
			:items="stepperItems"
			:ui="{
				header: 'justify-between',
				item: 'w-1/4',
				separator: 'start-[calc(75%+28px)] end-[calc(-225%+28px)]',
			}"
			size="sm"
		>
			<template #auth>
				<UAuthForm :schema="schema" :fields="fields" @submit="confirmAuthentication"/>
			</template>

			<template #personalization>
				<UPageCard variant="naked">
					<BasicSettings/>

					<UButton
						block
						:disabled="authenticationStatus != 'success'"
						@click="finish"
					>
						{{ t('finish') }}
					</UButton>
				</UPageCard>
			</template>
		</UStepper>
	</AuthContainer>
</template>
