<script setup lang="ts">
import { useFlowPanelStore } from "~/stores/flowPanel.ts"
import { ControlButton } from "@vue-flow/controls"

const props = defineProps<{
	panel: FlowPanel
	icon: string
	openIcon?: string
}>()

const { isOpen: isSpecificPanelOpen, toggle } = useFlowPanelStore()

const isOpen = computed(() => isSpecificPanelOpen(props.panel))
</script>

<template>
	<ControlButton :active="isOpen" @click="toggle(props.panel)">
		<UIcon :name="isOpen ? (openIcon ?? icon) : icon"/>
	</ControlButton>
</template>
