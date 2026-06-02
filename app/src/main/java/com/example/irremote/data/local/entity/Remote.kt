package com.example.irremote.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 遥控器实体
 */
@Entity(
    tableName = "remotes",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deviceId"])]
)
data class Remote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: Long,            // 关联设备ID
    val name: String,              // 遥控器名称
    val configJson: String,        // 遥控器配置JSON
    val isDefault: Boolean = false, // 是否是默认遥控器
    val createdAt: Long = System.currentTimeMillis()
)
