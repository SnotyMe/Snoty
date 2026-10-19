import type { JobTriggerResult } from "~/api/backend/generated"
import { useFlowExecutionEvent } from "~/composables/flow/useFlowExecutionSse.ts"

export const useNodeTriggerStateMachine = (runTrigger: (data: []) => Promise<JobTriggerResult | undefined>) => {
	const triggering = ref<boolean>(false)
	const runningJobId = ref<string | null>()

	const onTrigger = async () => {
		triggering.value = true
		try {
			const triggerResult = await runTrigger([])
			runningJobId.value = triggerResult?.jobId ?? null
		} finally {
			triggering.value = false
		}
	}

	useFlowExecutionEvent("FlowStarted", (_, data) => {
		const waitingFor = runningJobId.value
		if (waitingFor === null || data.jobId === waitingFor) {
			runningJobId.value = undefined
		}
	})

	const loading = computed(() => triggering.value || runningJobId.value !== undefined)

	return {
		onTrigger,
		loading,
	}
}
