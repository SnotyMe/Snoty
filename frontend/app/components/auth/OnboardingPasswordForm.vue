<i18n lang="yaml">
en:
  password:
    label: Password
    placeholder: Enter your password
  passwordConfirm:
    label: Confirm Password
    placeholder: Enter the same password again
de:
  password:
    label: Passwort
    placeholder: Dein Passwort
  passwordConfirm:
    label: Passwort (Bestätigung)
    placeholder: Das selbe Passwort noch einmal
</i18n>

<script setup lang="ts">
import type { AuthFormField } from "@nuxt/ui"
import { z } from "zod"

const emit = defineEmits<{
	(e: "submit", data: Schema): void
}>()

const { t } = useI18n({ useScope: "local" })

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

const schema = z.object({
	password: z.string(),
	passwordConfirm: z.string(),
}).refine(data => data.password === data.passwordConfirm, {
	message: "Passwords do not match",
	path: ["passwordConfirm"],
})

export type Schema = z.output<typeof schema>
</script>

<template>
	<UAuthForm :schema="schema" :fields="fields" @submit="ev => emit('submit', ev.data)"/>
</template>
