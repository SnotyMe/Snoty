<script setup lang="ts">
import type { AuthComposable } from "#layers/base/app/types/auth.ts"
import type { DropdownMenuItem } from "@nuxt/ui"

const props = defineProps<{
	items: DropdownMenuItem[][]
	useAuth: AuthComposable
	collapsed?: boolean
}>()

const { user } = props.useAuth()
const userDisplayName = computed(() =>
	user?.displayName ?? user?.name ?? `${user?.firstName ?? ""} ${user?.lastName ?? ""}`.trim(),
)
</script>

<template>
	<UDropdownMenu
		:items="items"
		:content="{ align: 'center', collisionPadding: 12 }"
		:ui="{ content: collapsed ? 'w-48' : 'w-(--reka-dropdown-menu-trigger-width)' }"
	>
		<UButton
			:avatar="{ alt: userDisplayName, size: 'md' }"
			:label="collapsed ? undefined : userDisplayName"
			:trailing-icon="collapsed ? undefined : 'i-lucide-chevrons-up-down'"
			color="neutral"
			variant="ghost"
			block
			:ui="{
				trailingIcon: 'text-dimmed',
			}"
		/>
	</UDropdownMenu>
</template>
