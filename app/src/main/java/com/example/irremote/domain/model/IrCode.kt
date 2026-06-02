package com.example.irremote.domain.model

/**
 * 红外编码模型
 */
data class IrCode(
    val protocol: String,       // "NEC", "RC5", "RC6", "RAW"等
    val address: Int,           // 设备地址
    val command: Int,           // 命令码
    val rawPattern: IntArray? = null  // 原始脉冲（用于未知协议或自定义）
) {
    companion object {
        // 常见协议
        const val PROTOCOL_NEC = "NEC"
        const val PROTOCOL_RC5 = "RC5"
        const val PROTOCOL_RC6 = "RC6"
        const val PROTOCOL_RAW = "RAW"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IrCode
        if (protocol != other.protocol) return false
        if (address != other.address) return false
        if (command != other.command) return false
        if (rawPattern != null) {
            if (other.rawPattern == null) return false
            if (!rawPattern.contentEquals(other.rawPattern)) return false
        } else if (other.rawPattern != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = protocol.hashCode()
        result = 31 * result + address
        result = 31 * result + command
        result = 31 * result + (rawPattern?.contentHashCode() ?: 0)
        return result
    }
}
