import type { locales } from "#layers/base/shared/i18n"

export type Locale = typeof locales[number]["code"]
