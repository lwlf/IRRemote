package com.example.irremote.domain.model

/**
 * 遥控器按键模型
 */
data class RemoteButton(
    val id: String,                    // 按键唯一标识
    val label: String,                 // 显示文本
    val icon: String? = null,          // 图标名称
    val irCode: IrCode,                // 红外编码
    val row: Int,                      // 行位置
    val col: Int,                      // 列位置
    val rowSpan: Int = 1,              // 占用行数
    val colSpan: Int = 1,              // 占用列数
    val backgroundColor: Long? = null, // 背景颜色
    val isToggle: Boolean = false      // 是否是切换按钮
)
