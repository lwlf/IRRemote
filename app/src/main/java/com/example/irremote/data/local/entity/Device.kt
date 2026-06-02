package com.example.irremote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 设备实体
 */
@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,              // 设备名称
    val brand: String? = null,     // 品牌
    val type: String,              // 设备类型: "tv", "ac", "fan"等
    val iconName: String? = null,  // 设备图标
    val createdAt: Long = System.currentTimeMillis()
)
