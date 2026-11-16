<i18n lang="yaml">
en:
  customize: Customize your Experience
  finish: Finish Onboarding
de:
  customize: Konfiguriere die Grundeinstellungen
  finish: Onboarding abschließen
</i18n>

<script setup lang="ts">
import type { StepperItem } from "@nuxt/ui"
import BasicSettings from "~/components/settings/BasicSettings.vue"
import { useAuth } from "~/stores/auth.ts"
import { jwtDecode } from "jwt-decode"
import OnboardingPasswordForm, { type Schema } from "~/components/auth/OnboardingPasswordForm.vue"

definePageMeta({
	layout: "center",
})

const route = useRoute()
const token = route.query.token as string
interface OnboardingToken {
	clientId: string
	email: string
	displayName: string
}
const onboardingToken = ifDefined(token, jwtDecode<OnboardingToken>)

const authStore = useAuth()
const { user, authProvider, authMetadata } = storeToRefs(authStore)
const multiStep = computed(() => onboardingToken && authProvider.value?.onboardingMode == "verify-email")

const { email, displayName } = onboardingToken ?? user.value!

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

const [
	DefinePersonalization,
	ReusePersonalization,
] = createReusableTemplate()
const stepper = useTemplateRef("stepper")

const finish = () => {
	navigateTo("/")
}

const confirmAuthentication = async (payload: Schema) => {
	stepper.value?.next()

	const metadata = authMetadata.value as { onboardingUrl?: string } | undefined
	if (metadata?.onboardingUrl == undefined) {
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
				clientId: onboardingToken?.clientId,
				password: payload.password,
			}),
		})
		if (!response.ok) throw Error("Response was not ok")

		await authStore.login({ email, password: payload.password })
	} catch {
		authenticationStatus.value = "error"
		return
	}

	authenticationStatus.value = "success"
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

		<DefinePersonalization>
			<UPageCard variant="naked">
				<BasicSettings/>

				<UButton
					block
					:disabled="multiStep && authenticationStatus != 'success'"
					@click="finish"
				>
					{{ t('finish') }}
				</UButton>
			</UPageCard>
		</DefinePersonalization>

		<UStepper
			v-if="multiStep"
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
				<OnboardingPasswordForm v-model="authenticationStatus" @submit="confirmAuthentication"/>
			</template>

			<template #personalization>
				<ReusePersonalization/>
			</template>
		</UStepper>
		<template v-else>
			<h1 class="flex items-center gap-2"><UIcon name="i-lucide-palette"/> {{ t('customize') }}</h1>
			<ReusePersonalization/>
		</template>
	</AuthContainer>
</template>
