export enum FlowPanel {
	NodeAdd = "NodeAdd",
	Journal = "Journal",
}

export const useFlowPanelStore = defineStore("flowPanel", () => {
	const openPanel = ref<FlowPanel | undefined>(undefined)

	const open = (panel: FlowPanel | undefined) => {
		openPanel.value = panel
	}

	const close = () => {
		openPanel.value = undefined
	}

	const toggle = (panel: FlowPanel) => {
		if (openPanel.value === panel) {
			close()
		} else {
			open(panel)
		}
	}

	const isOpen = (panel: FlowPanel) => {
		return openPanel.value === panel
	}

	return {
		open,
		close,
		toggle,
		isOpen,
	}
})
