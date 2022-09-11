package fortunate.signature.penit.xml

import android.util.Xml
import fortunate.signature.penit.entities.*
import fortunate.signature.penit.model.Task
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

object XMLTaskUtils {

    fun readTaskFromFile(file: File?, folder: TaskFolder) : Task {
        val inputStream = FileInputStream(file)
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(inputStream, null)
        parser.next()
        return parseTask(parser, parser.name, folder)
    }

    fun readBackupFromStream(inputStream: InputStream): BackupTask {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(inputStream, null)

        var tasks = listOf<Task>()
        var deletedTasks = listOf<Task>()
        var finishedTasks = listOf<Task>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    XMLTags.Notes -> tasks = parseList(parser, XMLTags.Tasks, TaskFolder.TASKS)
                    XMLTags.DeletedNotes -> deletedTasks = parseList(parser, XMLTags.DeletedTasks, TaskFolder.DELETED)
                    XMLTags.ArchivedNotes -> finishedTasks = parseList(parser, XMLTags.FinishedTasks, TaskFolder.FINISHED)
                }
            }
        }

        return BackupTask(tasks, deletedTasks, finishedTasks)
    }

    private fun parseList(parser: XmlPullParser, rootTag: String, folder: TaskFolder): List<Task> {
        val list = ArrayList<Task>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val task = parseTask(parser, parser.name, folder)
                list.add(task)
            } else if (parser.eventType == XmlPullParser.END_TAG) {
                if (parser.name == rootTag) {
                    break
                }
            }
        }

        return list
    }

    private fun parseTask(parser: XmlPullParser, rootTag: String, folder: TaskFolder): Task {
        var description = String()
        var name = String()
        var dueDate = String()
        var timestamp = 0L
        var due = false
        val priority = Priority.NONE

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    XMLTags.Name -> name = parser.nextText()
                    XMLTags.Description -> description = parser.nextText()
                    XMLTags.DueDate -> dueDate = parser.nextText()
                    XMLTags.Due -> due = parser.nextText().toBoolean()
                    XMLTags.Timestamp -> timestamp = parser.nextText().toLong()
                }
            } else if (parser.eventType == XmlPullParser.END_TAG) {
                if (parser.name == rootTag) {
                    break
                }
            }
        }

        return Task.createTask(0, folder, name, description, due, priority, timestamp, dueDate)
    }

    fun writeBackupToStream(backup: BackupTask, stream: OutputStream) {
        val xmlSerializer = Xml.newSerializer()

        xmlSerializer.setOutput(stream, null)
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        xmlSerializer.startDocument("UTF-8", true)

        xmlSerializer.startTag(null, XMLTags.ExportedNotes)

        appendBackupList(XMLTags.Notes, xmlSerializer, backup.tasks)
        appendBackupList(XMLTags.ArchivedNotes, xmlSerializer, backup.finishedTasks)
        appendBackupList(XMLTags.DeletedNotes, xmlSerializer, backup.deletedTasks)

        xmlSerializer.endTag(null, XMLTags.ExportedNotes)

        xmlSerializer.endDocument()
    }

    fun writeTaskToStream(task: Task, stream: OutputStream) {
        val xmlSerializer = Xml.newSerializer()

        xmlSerializer.setOutput(stream, null)
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        xmlSerializer.startDocument("UTF-8", true)

        appendNote(task, xmlSerializer)

        xmlSerializer.endDocument()
    }

    private fun appendNote(task: Task, xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag(null, XMLTags.Note)

        xmlSerializer.writeTagContent(XMLTags.DateCreated, task.timestamp.toString())
        xmlSerializer.writeTagContent(XMLTags.Due, task.due.toString())
        xmlSerializer.writeTagContent(XMLTags.Name, task.name)
        xmlSerializer.writeTagContent(XMLTags.Description, task.description)

        xmlSerializer.endTag(null, XMLTags.Task)
    }

    private fun appendBackupList(rootTag: String, xmlSerializer: XmlSerializer, list: List<Task>) {
        if (list.isNotEmpty()) {
            xmlSerializer.startTag(null, rootTag)

            list.forEach { task ->
                appendNote(task, xmlSerializer)
            }

            xmlSerializer.endTag(null, rootTag)
        }
    }

    private fun XmlSerializer.attribute(name: String, value: String) {
        attribute(null, name, value)
    }

    private fun XmlSerializer.writeTagContent(tag: String, content: String) {
        startTag(null, tag)
        text(content)
        endTag(null, tag)
    }

    private fun XmlPullParser.getAttributeValue(attribute: String) = getAttributeValue(null, attribute)


}

class BackupTask(
    val tasks: List<Task>,
    val deletedTasks: List<Task>,
    val finishedTasks: List<Task>
)