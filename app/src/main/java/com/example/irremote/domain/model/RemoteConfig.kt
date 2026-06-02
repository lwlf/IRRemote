package com.example.irremote.domain.model

import com.google.gson.annotations.SerializedName

/**
 * 遥控器配置模型
 */
data class RemoteConfig(
    val name: String,                          // 遥控器名称
    val brand: String? = null,                 // 品牌
    val type: String,                          // 设备类型: "tv", "ac", "fan", "stb"等
    val protocol: String = IrCode.PROTOCOL_NEC, // 默认协议
    val address: String,                       // 设备地址（十六进制字符串）
    val frequency: Int = 38000,                // 红外频率（Hz）
    val buttons: List<ButtonConfig>,           // 按键配置列表
    val gridColumns: Int = 4                   // 网格列数
)

/**
 * 按钮配置
 */
data class ButtonConfig(
    val id: String,                            // 按键ID
    val label: String,                         // 显示文本
    val icon: String? = null,                  // 图标
    val command: String,                       // 命令码（十六进制字符串）
    val protocol: String? = null,              // 可选：单独覆盖协议
    val address: String? = null,               // 可选：单独覆盖地址
    val row: Int,                              // 行位置
    val col: Int,                              // 列位置
    @SerializedName("row_span")
    val rowSpan: Int = 1,
    @SerializedName("col_span")
    val colSpan: Int = 1,
    val rawPattern: List<Int>? = null          // 原始脉冲（用于RAW协议）
)
