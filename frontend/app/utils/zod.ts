import type { z } from "zod"

export const extendIf = <
	Base extends z.ZodRawShape,
	Extra extends z.ZodRawShape,
>(
	schema: z.ZodObject<Base>,
	matches: boolean,
	extra: Extra,
) => {
	return matches
		? schema.extend(extra)
		: schema
}
