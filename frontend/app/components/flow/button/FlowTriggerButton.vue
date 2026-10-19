<script setup lang="ts">
import { ControlButton } from "@vue-flow/controls"
import { type NodeMetadata, NodeMetadataStereotypeEnum } from "~/api/backend/generated"
import { useFlowNode } from "~/composables/node/useFlowNode.ts"
import { useNodeTriggerStateMachine } from "~/composables/node/useNodeTriggerStateMachine.ts"
import { useNodeMetadataStore } from "~/stores/nodeMetadata.ts"
import { useFlowState } from "~/composables/flow/useFlowState.ts"

const { nodes } = useFlowState()
const { metadata } = storeToRefs(useNodeMetadataStore())

const entrypoint = computed(() => {
	const metadatas = metadata.value

	const triggerNodes = nodes.value?.map((node) => {
		const nodeMetadata = metadatas?.find(metadata => metadata.type === node.data.type)?.metadata
		return {
			node: node.data,
			metadata: nodeMetadata,
		}
	})?.filter((item): item is typeof item & { metadata: NodeMetadata } =>
		item.metadata?.stereotype == NodeMetadataStereotypeEnum.Trigger,
	)

	if (triggerNodes?.length == 1) {
		const { node, metadata } = triggerNodes[0]!
		if (metadata.output == undefined || metadata.output.length == 0) {
			return node
		}
	}

	return undefined
})

const { loading, onTrigger } = useNodeTriggerStateMachine(input =>
	useFlowNode(entrypoint.value.id).trigger(input),
)

const appConfig = useAppConfig()
</script>

<template>
	<ControlButton v-if="entrypoint" :disabled="loading" @click="onTrigger">
		<UIcon :name="loading ? appConfig.ui.icons.loading : 'i-lucide-rocket'" :class="{ 'animate-spin': loading }"/>
	</ControlButton>
</template>
