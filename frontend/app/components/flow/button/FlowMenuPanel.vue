<script setup lang="ts">
import type { FlowPanel } from "~/stores/flowPanel.ts"
import { useFlowPanelStore } from "~/stores/flowPanel.ts"

const props = defineProps<{
	panel: FlowPanel
	verticalAlign?: "top" | "bottom"
	horizontalAlign?: "left" | "right"
	width: string
	height: string
	class?: string
	innerClass?: string
}>()

const horizontalAlign = computed(() => props.horizontalAlign ?? "left")
const verticalAlign = computed(() => props.verticalAlign ?? "top")

const containerStyle = computed(() => ({
	[horizontalAlign.value]: "var(--x-padding)",
	[verticalAlign.value]: "var(--y-padding)",
	width: props.width,
	height: props.height,
}))

const { close, isOpen: isSpecifiedPanelOpen } = useFlowPanelStore()
const isOpen = computed(() => isSpecifiedPanelOpen(props.panel))
</script>

<template>
	<div
		v-show="isOpen"
		id="container"
		:class="[
			horizontalAlign === 'right' && 'rtl',
			props.class,
			'z-10 p-2 flow-node card bg-muted border border-accented rounded overflow-hidden block',
		]"
		:style="containerStyle"
	>
		<div :class="innerClass">
			<div class="w-full flex justify-between items-center gap-2">
				<div class="flex-1 flex items-center gap-1">
					<UButton
						v-if="horizontalAlign == 'right'"
						variant="subtle"
						icon="i-lucide-x"
						@click="close"
					/>
					<slot name="header"/>
				</div>
				<div class="flex items-center gap-1">
					<slot name="actions"/>
					<UButton
						v-if="horizontalAlign == 'left'"
						variant="subtle"
						icon="i-lucide-x"
						@click="close"
					/>
				</div>
			</div>
			<slot/>
		</div>
	</div>
</template>

<style scoped>
#container {
	position: absolute;

	--y-padding: calc(var(--flow-control-padding) + 0.5em + var(--flow-control-button-size));
	--x-padding: var(--flow-control-padding);

	max-width: calc(100% - 2 * var(--x-padding));
	max-height: calc(100% - var(--y-padding) - var(--flow-control-padding));

	user-select: text;
	-moz-user-select: text;
	-webkit-user-select: text;

	resize: both;
}

#container.rtl {
	direction: rtl;
}

#container > div {
	direction: ltr;
	height: 100%;
	overflow-y: auto;
}
</style>
