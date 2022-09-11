package fortunate.signature.penit.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fortunate.signature.penit.data.dao.*
import fortunate.signature.penit.model.*

@TypeConverters(Converters::class)
@Database(entities = [BaseNote::class, Label::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract val labelDao: LabelDao
    abstract val commonDao: CommonDao
    abstract val baseNoteDao: BaseNoteDao

    companion object {

        private const val databaseName = "NoteDatabase2"

        @Volatile
        private var instance: NoteDatabase? = null

        fun getDatabase(application: Application): NoteDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(application, NoteDatabase::class.java, databaseName).build()
                Companion.instance = instance
                return instance
            }
        }
    }

}