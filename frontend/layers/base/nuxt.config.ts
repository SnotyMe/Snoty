import { locales } from "./shared/i18n.ts"
import { fileURLToPath } from "node:url"

export default defineNuxtConfig({
	modules: [
		"@nuxtjs/i18n",
	],

	$meta: {
		name: "base",
	},

	css: [
		fileURLToPath(
			new URL("./app/assets/css/base.css", import.meta.url),
		),
	],

	i18n: {
		locales: locales,
		strategy: "no_prefix",
		defaultLocale: "en",
	},
})
