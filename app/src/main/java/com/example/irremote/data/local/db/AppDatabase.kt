package com.example.irremote.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.local.entity.Remote

/**
 * Room数据库
 */
@Database(
    entities = [Device::class, Remote::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun remoteDao(): RemoteDao
}
