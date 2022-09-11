package fortunate.signature.penit.data

import androidx.room.TypeConverter
import fortunate.signature.penit.entities.*
import fortunate.signature.penit.model.*
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)

    @TypeConverter
    fun toString(taskState: TaskState?): String? = taskState?.name

    @TypeConverter
    fun toTaskState(name: String?): TaskState =
        name?.let { enumValueOf<TaskState>(it) } ?: TaskState.DOING

    @TypeConverter
    fun labelsToJSON(labels: HashSet<String>) = JSONArray(labels).toString()

    @TypeConverter
    fun jsonToLabels(json: String) = JSONArray(json).iterable<String>().toHashSet()

    @TypeConverter
    fun spansToJSON(spans: List<SpanRepresentation>): String {
        val objects = spans.map { spanRepresentation -> spanRepresentation.toJSONObject() }
        return JSONArray(objects).toString()
    }

    @TypeConverter
    fun jsonToSpans(json: String): List<SpanRepresentation> {
        val iterable = JSONArray(json).iterable<JSONObject>()
        return iterable.map { jsonObject -> SpanRepresentation.fromJSONObject(jsonObject) }
    }

    @TypeConverter
    fun itemsToJson(items: List<ListItem>): String {
        val objects = items.map { listItem -> listItem.toJSONObject() }
        return JSONArray(objects).toString()
    }

    @TypeConverter
    fun jsonToItems(json: String): List<ListItem> {
        val iterable = JSONArray(json).iterable<JSONObject>()
        return iterable.map { jsonObject -> ListItem.fromJSONObject(jsonObject) }
    }

    private fun <T> JSONArray.iterable(): Iterable<T> {
        return Iterable {
            object : Iterator<T> {
                var nextIndex = 0

                override fun next(): T {
                    val element = get(nextIndex)
                    nextIndex++
                    return element as T
                }

                override fun hasNext(): Boolean {
                    return nextIndex < length()
                }
            }
        }
    }

}