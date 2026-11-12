<script setup lang="ts">
import type { DropdownMenuItem, NavigationMenuItem } from "@nuxt/ui"
import type { AuthComposable } from "#layers/base/app/types/auth.ts"

defineProps<{
	links: [NavigationMenuItem[], NavigationMenuItem[]]
	userMenu: [DropdownMenuItem[], DropdownMenuItem[]]
	useAuth: AuthComposable
}>()

const ui = (collapsed: boolean) => {
	const buttonPropsArray = ["p-1.5"]
	if (collapsed) buttonPropsArray.push("*:mx-auto")

	const buttonProps = buttonPropsArray.join(" ")

	return { button: { base: buttonProps }, navigationMenu: { link: buttonProps } }
}
</script>

<template>
	<UDashboardSidebar
		collapsible
		resizable
		:min-size="15"
		:default-size="15"
		:max-size="25"
		:ui="{ footer: 'border-t border-default' }"
		class="bg-muted min-w-12"
	>
		<template #header="{ collapsed }">
			<div v-if="!collapsed" class="w-full flex items-center justify-between">
				<FrameworkAppLogoHeader class="shrink-0"/>
				<UDashboardSidebarCollapse/>
			</div>
			<div v-else class="group absolute">
				<FrameworkAppLogo class="block group-hover:hidden"/> <!-- absolute ignores single-pixel cutoff => no layout shift -->
				<UDashboardSidebarCollapse class="not-group-hover:hidden!"/>
			</div>
		</template>

		<template #default="{ collapsed }">
			<UTheme :ui="ui(collapsed)">
				<UDashboardSearchButton :collapsed="collapsed" size="md"/>

				<UNavigationMenu
					:collapsed="collapsed"
					:items="links[0]"
					:ui="{ link: collapsed ? 'mt-2.5 mb-5' : 'py-4', label: 'pl-1 pb-0' }"
					orientation="vertical"
				/>

				<UNavigationMenu
					:collapsed="collapsed"
					:items="links[1]"
					orientation="vertical"
					class="mt-auto"
				/>
			</UTheme>
		</template>

		<template #footer="{ collapsed }">
			<FrameworkUserMenu :items="userMenu" :use-auth="useAuth" :collapsed="collapsed"/>
		</template>
	</UDashboardSidebar>
</template>
