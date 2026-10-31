export function buildUrl(path: string, params: Record<string, string | undefined> = {}) {
	const searchParams = new URLSearchParams()

	// eslint-disable-next-line array-element-newline
	Object.entries(params).forEach(([key, value]) => {
		if (!value || value == "") {
			return
		}

		searchParams.set(key, value)
	})

	return `${path}?${searchParams}`
}
