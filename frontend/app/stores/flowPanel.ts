export enum FlowPanel {
	NodeAdd = "NodeAdd",
	Journal = "Journal",
}

export const useFlowPanelStore = defineStore("flowPanel", () => {
	const openPanel = ref<FlowPanel | undefined>(undefined)

	watch(openPanel, o => console.log("open", o))

	const open = (panel: FlowPanel | undefined) => {
		console.log("runnig open", panel)
		openPanel.value = panel
	}

	const close = () => {
		openPanel.value = undefined
	}

	const toggle = (panel: FlowPanel) => {
		console.log("toggle", panel)
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
