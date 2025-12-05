package com.dnfapps.arrmatey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dnfapps.arrmatey.api.arr.model.ArrSeries

@Dao
interface SeriesDao: BaseArrDao<ArrSeries> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(items: List<ArrSeries>)

    @Query("DELETE FROM arr_series")
    override suspend fun clearAll()

    @Query("SELECT * FROM arr_series WHERE instanceId = :instanceId")
    override suspend fun getAll(instanceId: Long): List<ArrSeries>

}