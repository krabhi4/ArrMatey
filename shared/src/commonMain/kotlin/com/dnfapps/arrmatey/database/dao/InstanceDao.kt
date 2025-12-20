package com.dnfapps.arrmatey.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.Flow

@Dao
interface InstanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(instance: Instance): Long

    @Delete
    suspend fun delete(instance: Instance)

    @Update
    suspend fun update(instance: Instance): Int

    @Query("SELECT * FROM instances")
    fun observeAllInstances(): Flow<List<Instance>>

    @Query("""
        UPDATE instances 
        SET selected = CASE 
            WHEN id = :id THEN true
            ELSE false
        END
        WHERE type = :type
    """)
    suspend fun setInstanceAsSelected(id: Long, type: InstanceType)

    @Query("SELECT id FROM instances WHERE url = :url")
    suspend fun findByUrl(url: String): Long?

    @Query("SELECT id FROM instances WHERE label = :label")
    suspend fun findByLabel(label: String): Long?

    @Query("SELECT id FROM instances WHERE url = :url AND id != :currentId LIMIT 1")
    suspend fun findOtherByUrl(url: String, currentId: Long): Long?

    @Query("SELECT id FROM instances WHERE label = :label AND id != :currentId LIMIT 1")
    suspend fun findOtherByLabel(label: String, currentId: Long): Long?

    @Query("""
        UPDATE instances
        SET selected = true
        WHERE id = (
            SELECT id
            FROM instances AS i
            WHERE i.type = :type
                AND NOT EXISTS (
                    SELECT 1
                    FROM instances AS j
                    WHERE j.type = :type
                        AND j.selected = true
                )
            ORDER BY i.id
            LIMIT 1
        )
    """)
    suspend fun ensureFirstSelectedIfNone(type: InstanceType)

    @Transaction
    suspend fun deleteAndUpdateSelected(instance: Instance) {
        val type = instance.type
        delete(instance)
        ensureFirstSelectedIfNone(type)
    }
}
