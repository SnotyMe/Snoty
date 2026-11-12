<script setup lang="ts">
import AppRoot from "#layers/base/app/components/framework/AppRoot.vue"
import { useNavigation, useUserMenu } from "#imports"

const { links } = useNavigation()
const { items: userMenu } = useUserMenu()
</script>

<template>
	<AppRoot :links="links" :user-menu="userMenu" :use-auth="useAuth">
		<slot/>
	</AppRoot>
</template>
