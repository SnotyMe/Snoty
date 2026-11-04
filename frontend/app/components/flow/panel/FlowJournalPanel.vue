<script setup lang="ts">
import type {
	EnumeratedFlowExecutionStatusEnum,
	FlowExecution,
	FlowExecutionEventFlowEndedEvent,
	FlowExecutionEventFlowLogEvent,
	FlowExecutionEventFlowStartedEvent,
	NodeLogEntryDto,
} from "~/api/backend/generated"
import { FlowNodeLogLevelEnum } from "~/api/backend/generated"
import LogLevelSelect from "~/components/primitives/LogLevelSelect.vue"
import { useFlowExecutions } from "~/composables/flow/useFlowExecutions.ts"
import DateTimeFormat from "~/components/primitives/DateTimeFormat.vue"
import LogTable from "~/components/logs/LogTable.vue"
import FlowExecutionStatus from "~/components/flow/FlowExecutionStatus.vue"
import FlowMenuPanel from "~/components/flow/panel/FlowMenuPanel.vue"

const props = defineProps<{
	flowId: string
}>()

const filterLogLevel = ref<FlowNodeLogLevelEnum[]>(Object.values(FlowNodeLogLevelEnum))

const { getFlowExecutions, connect, disconnect } = useFlowExecutions(props.flowId)

const { data: executions } = useAsyncData(() => getFlowExecutions() as Promise<FlowExecution[]>, { deep: true })

onMounted(async () => {
	const eventSource = await connect()

	eventSource.addEventListener("FlowStarted", (event) => {
		const data: FlowExecutionEventFlowStartedEvent & Required<Pick<FlowExecutionEventFlowStartedEvent, "timestamp">> = JSON.parse(event.data)

		executions.value?.unshift(data)
	})

	eventSource.addEventListener("FlowLog", (event) => {
		const { jobId, entry }: FlowExecutionEventFlowLogEvent = JSON.parse(event.data)

		const execution = executions.value?.find(it => it.jobId === jobId)
		if (!execution) return

		execution.logs ??= []
		execution.logs.unshift(entry)
	})

	eventSource.addEventListener("FlowEnded", (event) => {
		const data: FlowExecutionEventFlowEndedEvent = JSON.parse(event.data)

		const execution = executions.value?.find(it => it.jobId === data.jobId)
		if (!execution) return

		execution.status = data.status
	})
})

onUnmounted(() => {
	disconnect()
})
</script>

<template>
	<FlowMenuPanel
		:panel="FlowPanel.Journal"
		horizontal-align="right"
		width="min(80em, 100%)"
		height="min(60em, 100%)"
		inner-class="flex flex-col gap-2"
	>
		<template #actions>
			<LogLevelSelect v-model="filterLogLevel" multiple class="w-52"/>
		</template>

		<div class="overflow-auto shrink">
			<div v-for="execution in executions ?? []" :key="execution.jobId" class="mb-4">
				<div class="flex items-center gap-2">
					<FlowExecutionStatus :value="execution.status as EnumeratedFlowExecutionStatusEnum" size="xs" variant="link"/>
					<span>
						{{ execution.triggeredBy }}
						<span class="text-muted">
							•
						</span>
						<DateTimeFormat :date="execution.timestamp"/>
					</span>
				</div>

				<LogTable
					v-if="execution.logs"
					:logs="execution.logs.filter((log: NodeLogEntryDto) => filterLogLevel.includes(log.level))"
				/>
			</div>
		</div>
	</FlowMenuPanel>
</template>
