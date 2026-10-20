package me.snoty.backend.database.mongo.observability

import me.snoty.backend.utils.orNull
import org.bson.BsonDocument
import org.bson.BsonValue

// See https://docs.mongodb.com/manual/reference/command for the command reference
val COMMANDS_WITH_COLLECTION_NAME: Set<String> = LinkedHashSet(
	mutableListOf(
		"aggregate", "count", "distinct", "mapReduce", "geoSearch", "delete", "find", "findAndModify",
		"insert", "update", "collMod", "compact", "convertToCapped", "create", "createIndexes", "drop",
		"dropIndexes", "killCursors", "listIndexes", "reIndex"
	)
)

fun getCollectionName(command: BsonDocument, commandName: String): String? {
	if (COMMANDS_WITH_COLLECTION_NAME.contains(commandName)) {
		val collectionName = getNonEmptyBsonString(command[commandName])
		if (collectionName != null) {
			return collectionName
		}
	}
	// Some other commands, like getMore, have a field like {"collection": collectionName}.
	return getNonEmptyBsonString(command["collection"])
}

/**
 * @return trimmed string from `bsonValue` or null if the trimmed string was
 * empty or the value wasn't a string
 */
fun getNonEmptyBsonString(bsonValue: BsonValue?): String? {
	if (bsonValue == null || !bsonValue.isString) {
		return null
	}
	val stringValue = bsonValue.asString().value.trim { it <= ' ' }
	return stringValue.orNull()
}
