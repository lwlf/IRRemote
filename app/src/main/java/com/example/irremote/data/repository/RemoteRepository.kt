package com.example.irremote.data.repository

import com.example.irremote.data.local.db.DeviceDao
import com.example.irremote.data.local.db.RemoteDao
import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.local.entity.Remote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 遥控器数据仓库
 */
@Singleton
class RemoteRepository @Inject constructor(
    private val deviceDao: DeviceDao,
    private val remoteDao: RemoteDao
) {
    // 设备相关操作
    fun getAllDevices(): Flow<List<Device>> = deviceDao.getAllDevices()

    suspend fun getDeviceById(deviceId: Long): Device? = deviceDao.getDeviceById(deviceId)

    suspend fun insertDevice(device: Device): Long = deviceDao.insertDevice(device)

    suspend fun updateDevice(device: Device) = deviceDao.updateDevice(device)

    suspend fun deleteDevice(device: Device) = deviceDao.deleteDevice(device)

    suspend fun deleteDeviceById(deviceId: Long) = deviceDao.deleteDeviceById(deviceId)

    // 遥控器相关操作
    fun getRemotesByDeviceId(deviceId: Long): Flow<List<Remote>> =
        remoteDao.getRemotesByDeviceId(deviceId)

    suspend fun getRemoteById(remoteId: Long): Remote? = remoteDao.getRemoteById(remoteId)

    suspend fun getDefaultRemote(deviceId: Long): Remote? = remoteDao.getDefaultRemote(deviceId)

    suspend fun insertRemote(remote: Remote): Long = remoteDao.insertRemote(remote)

    suspend fun updateRemote(remote: Remote) = remoteDao.updateRemote(remote)

    suspend fun deleteRemote(remote: Remote) = remoteDao.deleteRemote(remote)

    suspend fun deleteRemoteById(remoteId: Long) = remoteDao.deleteRemoteById(remoteId)

    suspend fun setDefaultRemote(remoteId: Long) {
        val remote = remoteDao.getRemoteById(remoteId) ?: return
        remoteDao.clearDefaultForDevice(remote.deviceId)
        remoteDao.setAsDefault(remoteId)
    }
}
