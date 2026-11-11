<i18n lang="yaml">
en:
  search:
    placeholder: Search Flows...
  sort:
    lastExecution:
      asc: Oldest Executed
      desc: Latest Executed
    name:
      asc: Name (A-Z)
      desc: Name (Z-A)
    modifiedAt:
      desc: Last Modified
de:
  search:
    placeholder: Flows suchen...
  sort:
    lastExecution:
      asc: Älteste Ausführung
      desc: Letzte Ausführung
    name:
      asc: Name (A-Z)
      desc: Name (Z-A)
    modifiedAt:
      desc: Letzte Modifizierung
</i18n>

<script setup lang="ts">
import { useFlowFiltersStore } from "~/stores/flowFilters"
import SearchField from "~/components/ui/SearchField.vue"

const { t } = useI18n({ useScope: "local" })

const flowFiltersStore = useFlowFiltersStore()
const { query, sort } = storeToRefs(flowFiltersStore)
const sortOptions = flowFiltersStore.sortOptions.map(it => ({
	...it,
	label: t(`sort.${it.key}.${it.desc ? "desc" : "asc"}`),
}))
</script>

<template>
	<div class="flex justify-between">
		<SearchField v-model="query" :placeholder="t('search.placeholder')"/>
		<USelect
			v-model="sort"
			:items="sortOptions"
			:icon="sort.icon"
			value-key="id"
			class="w-3xs"
		/>
	</div>
</template>
