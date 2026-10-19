<script setup lang="ts">
import type { JobTriggerResult } from "~/api/backend/generated"
import { useNodeTriggerStateMachine } from "~/composables/node/useNodeTriggerStateMachine.ts"

const emit = defineEmits<{
	(e: "trigger", input: object[]): Promise<JobTriggerResult | undefined>
}>()

const { loading, onTrigger } = useNodeTriggerStateMachine(data => emit("trigger", data))
</script>

<template>
	<UButton
		icon="i-lucide-play"
		variant="ghost"
		size="sm"
		:disabled="loading"
		:loading="loading"
		@click="onTrigger"
	/>
</template>
