<script setup lang="ts">
import DotGrid from "~/components/vue-bits/DotGrid.vue"
import AppLogo from "#layers/base/app/components/framework/AppLogo.vue"

defineProps<{
	ui?: {
		wrapper?: string
	}
}>()
</script>

<template>
	<UPageCard
		class="w-4xl max-w-9/10 min-h-140"
		:ui="{
			container: 'grid grid-cols-1 md:grid-cols-2',
			leading: 'size-full relative',
			wrapper: 'not-md:hidden',
		}"
	>
		<div class="relative" :class="ui?.wrapper ?? ''">
			<slot/>
		</div>

		<template #leading>
			<div class="size-full absolute flex justify-center items-center">
				<AppLogo class="size-32 bg-default z-1"/>
			</div>

			<DotGrid
				:dot-size="5"
				:gap="50"
				base-color="#303030"
				active-color="#FFFFFF"
				:proximity="120"
				:speed-trigger="100"
				:shock-radius="250"
				:shock-strength="5"
				:max-speed="5000"
				:resistance="750"
				:return-duration="1.5"
				class="size-full"
			/>
		</template>
	</UPageCard>
</template>
