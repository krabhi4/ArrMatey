package com.dnfapps.arrmatey.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.Flow

@Dao
interface InstanceDao {

    @Insert
    suspend fun insert(instance: Instance)

    @Delete
    suspend fun delete(instance: Instance)

    @Update
    suspend fun update(instance: Instance)

    @Query("SELECT * FROM instances")
    fun getAllAsFlow(): Flow<List<Instance>>

    @Query("SELECT * FROM instances WHERE type = 0")
    fun getAllSonarrInstancesAsFlow(): Flow<List<Instance>>

    @Query("SELECT * FROM instances WHERE type = :instanceType ORDER BY id ASC LIMIT 1")
    fun getFirstInstance(instanceType: InstanceType): Flow<Instance?>
}
