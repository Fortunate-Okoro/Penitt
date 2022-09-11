package fortunate.signature.penit.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fortunate.signature.penit.data.dao.TaskDao
import fortunate.signature.penit.model.Task

@TypeConverters(Converters::class)
@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object {

        private const val databaseName = "TaskDatabase2"

        @Volatile
        private var instance: TaskDatabase? = null

        fun getDatabase(application: Application): TaskDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(application, TaskDatabase::class.java, databaseName).build()
                Companion.instance = instance
                return instance
            }
        }
    }


}