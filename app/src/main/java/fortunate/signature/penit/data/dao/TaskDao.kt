package fortunate.signature.penit.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import fortunate.signature.penit.entities.TaskFolder
import fortunate.signature.penit.model.Task

@Dao
interface TaskDao {

    @Delete
    suspend fun delete(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) : Long

    @Insert
    suspend fun insert(task: List<Task>)

    @Update
    suspend fun update(task: List<Task>)

    @Query("DELETE FROM task WHERE folder = :folder")
    suspend fun deleteFrom(folder: TaskFolder)

    @Query("SELECT * FROM task WHERE folder = :folder ORDER BY due DESC, timestamp DESC")
    fun getFrom(folder: TaskFolder): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE folder = :folder ORDER BY due DESC, timestamp DESC")
    suspend fun getListFrom(folder: TaskFolder): List<Task>


    @Query("UPDATE task SET folder = :folder WHERE id = :id")
    suspend fun move(id: Long, folder: TaskFolder)


    @Query("UPDATE task SET due= :due WHERE id = :id")
    suspend fun updateDue(id: Long, due: Boolean)

    fun getTasksByKeyword(keyword: String): LiveData<List<Task>> {
        val result = getTasksByKeyword(keyword, TaskFolder.TASKS)
        return Transformations.map(result) { list -> list.filter { task -> matchesKeyword(task, keyword) } }
    }

    @Query("SELECT * FROM task WHERE folder = :folder AND (name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%') ORDER BY due DESC, timestamp DESC")
    fun getTasksByKeyword(keyword: String, folder: TaskFolder): LiveData<List<Task>>

    private fun matchesKeyword(task: Task, keyword: String): Boolean {
        if (task.name.contains(keyword, true)) {
            return true
        }
        if (task.description.contains(keyword, true)) {
            return true
        }
        return false
    }

}