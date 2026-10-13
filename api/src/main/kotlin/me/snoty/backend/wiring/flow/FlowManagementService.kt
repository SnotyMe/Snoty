package me.snoty.backend.wiring.flow

import me.snoty.core.flow.Workflow

interface FlowManagementService {
	suspend fun deleteFlowCascading(workflow: Workflow)
}
