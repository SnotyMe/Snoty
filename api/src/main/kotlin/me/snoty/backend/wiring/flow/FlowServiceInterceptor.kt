package me.snoty.backend.wiring.flow

import me.snoty.backend.hooks.HookRegistry
import me.snoty.core.flow.Workflow
import me.snoty.core.flow.WorkflowSettings
import me.snoty.core.user.UserId
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class FlowServiceInterceptor(
    @Named("adapter") private val delegate: FlowService,
    private val hookRegistry: HookRegistry,
) : FlowService by delegate {
    override suspend fun create(userId: UserId, name: String, settings: WorkflowSettings) =
        delegate.create(userId, name, settings).also {
            hookRegistry.executeHooks(FlowCreatedHook::class, it)
        }

    override suspend fun updateSettings(flow: Workflow, settings: WorkflowSettings) =
        delegate.updateSettings(flow, settings)
            .also {
                hookRegistry.executeHooks(FlowUpdatedHook::class, flow)
            }

    override suspend fun delete(flow: Workflow) =
        delegate.delete(flow).also {
            hookRegistry.executeHooks(FlowDeletedHook::class, flow)
        }
}
