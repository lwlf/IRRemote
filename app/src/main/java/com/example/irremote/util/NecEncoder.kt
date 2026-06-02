package com.example.irremote.util

/**
 * NEC协议编码器
 *
 * NEC协议格式：
 * - 引导码: 9ms高 + 4.5ms低
 * - 逻辑0: 562.5us高 + 562.5us低
 * - 逻辑1: 562.5us高 + 1687.5us低
 * - 重复码: 9ms高 + 2.25ms低 + 562.5us高
 */
object NecEncoder {

    // NEC协议时间常量（微秒）
    private const val HEADER_HIGH = 9000
    private const val HEADER_LOW = 4500
    private const val BIT_HIGH = 563
    private const val BIT_ZERO_LOW = 563
    private const val BIT_ONE_LOW = 1687
    private const val REPEAT_HEADER_HIGH = 9000
    private const val REPEAT_HEADER_LOW = 2250
    private const val REPEAT_BIT_HIGH = 563

    /**
     * 编码NEC协议命令
     * @param address 设备地址（0-255）
     * @param command 命令码（0-255）
     * @return 脉冲模式数组（微秒）
     */
    fun encode(address: Int, command: Int): IntArray {
        val pattern = mutableListOf<Int>()

        // 引导码
        pattern.add(HEADER_HIGH)
        pattern.add(HEADER_LOW)

        // 地址（8位，LSB first）
        for (i in 0 until 8) {
            pattern.add(BIT_HIGH)
            if ((address shr i) and 1 == 1) {
                pattern.add(BIT_ONE_LOW)
            } else {
                pattern.add(BIT_ZERO_LOW)
            }
        }

        // 地址取反（8位）
        val addressInverse = address xor 0xFF
        for (i in 0 until 8) {
            pattern.add(BIT_HIGH)
            if ((addressInverse shr i) and 1 == 1) {
                pattern.add(BIT_ONE_LOW)
            } else {
                pattern.add(BIT_ZERO_LOW)
            }
        }

        // 命令（8位，LSB first）
        for (i in 0 until 8) {
            pattern.add(BIT_HIGH)
            if ((command shr i) and 1 == 1) {
                pattern.add(BIT_ONE_LOW)
            } else {
                pattern.add(BIT_ZERO_LOW)
            }
        }

        // 命令取反（8位）
        val commandInverse = command xor 0xFF
        for (i in 0 until 8) {
            pattern.add(BIT_HIGH)
            if ((commandInverse shr i) and 1 == 1) {
                pattern.add(BIT_ONE_LOW)
            } else {
                pattern.add(BIT_ZERO_LOW)
            }
        }

        // 结束位
        pattern.add(BIT_HIGH)

        return pattern.toIntArray()
    }

    /**
     * 生成重复码
     * @return 重复码脉冲数组
     */
    fun encodeRepeat(): IntArray {
        return intArrayOf(
            REPEAT_HEADER_HIGH,
            REPEAT_HEADER_LOW,
            REPEAT_BIT_HIGH
        )
    }

    /**
     * 从十六进制字符串解析地址和命令
     */
    fun parseHexString(hex: String): Int {
        return hex.removePrefix("0x").removePrefix("0X").toInt(16)
    }
}
