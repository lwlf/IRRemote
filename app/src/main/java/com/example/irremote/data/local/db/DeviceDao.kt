package com.example.irremote.data.local.db

import androidx.room.*
import com.example.irremote.data.local.entity.Device
import kotlinx.coroutines.flow.Flow

/**
 * 设备数据访问对象
 */
@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY createdAt DESC")
    fun getAllDevices(): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: Long): Device?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device): Long

    @Update
    suspend fun updateDevice(device: Device)

    @Delete
    suspend fun deleteDevice(device: Device)

    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: Long)
}
