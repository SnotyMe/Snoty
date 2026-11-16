export const ifDefined = <I, O>(value: I | undefined, mapper: (value: I) => O) => {
	if (value === undefined) {
		return undefined
	}

	return mapper(value)
}
