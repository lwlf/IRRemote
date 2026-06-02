package com.example.irremote.util

import android.content.Context
import android.hardware.ConsumerIrManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 红外发射器工具类
 */
@Singleton
class IrTransmitter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val irManager: ConsumerIrManager? by lazy {
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    }

    /**
     * 检查设备是否有红外发射器
     */
    fun hasIrEmitter(): Boolean {
        return irManager?.hasIrEmitter() ?: false
    }

    /**
     * 获取支持的红外频率
     */
    fun getCarrierFrequencies(): List<Pair<Int, Int>> {
        return irManager?.carrierFrequencies?.map {
            it.minFrequency to it.maxFrequency
        } ?: emptyList()
    }

    /**
     * 发射红外信号
     * @param frequency 载波频率（Hz），空调通常是38000
     * @param pattern 脉冲模式（微秒数组）
     */
    fun transmit(frequency: Int, pattern: IntArray) {
        irManager?.transmit(frequency, pattern)
    }

    /**
     * 发送NEC协议命令
     * @param address 设备地址
     * @param command 命令码
     */
    fun sendNecCommand(address: Int, command: Int) {
        val pattern = NecEncoder.encode(address, command)
        transmit(38000, pattern)
    }

    /**
     * 发送原始脉冲
     * @param pattern 脉冲数组
     */
    fun sendRawPattern(pattern: IntArray) {
        transmit(38000, pattern)
    }
}
