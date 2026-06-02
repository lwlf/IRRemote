package com.example.irremote.data.local.db

import androidx.room.*
import com.example.irremote.data.local.entity.Remote
import kotlinx.coroutines.flow.Flow

/**
 * 遥控器数据访问对象
 */
@Dao
interface RemoteDao {

    @Query("SELECT * FROM remotes WHERE deviceId = :deviceId ORDER BY isDefault DESC, createdAt DESC")
    fun getRemotesByDeviceId(deviceId: Long): Flow<List<Remote>>

    @Query("SELECT * FROM remotes WHERE id = :remoteId")
    suspend fun getRemoteById(remoteId: Long): Remote?

    @Query("SELECT * FROM remotes WHERE deviceId = :deviceId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultRemote(deviceId: Long): Remote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemote(remote: Remote): Long

    @Update
    suspend fun updateRemote(remote: Remote)

    @Delete
    suspend fun deleteRemote(remote: Remote)

    @Query("DELETE FROM remotes WHERE id = :remoteId")
    suspend fun deleteRemoteById(remoteId: Long)

    @Query("UPDATE remotes SET isDefault = 0 WHERE deviceId = :deviceId")
    suspend fun clearDefaultForDevice(deviceId: Long)

    @Query("UPDATE remotes SET isDefault = 1 WHERE id = :remoteId")
    suspend fun setAsDefault(remoteId: Long)
}
