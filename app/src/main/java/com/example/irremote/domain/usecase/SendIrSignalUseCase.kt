package com.example.irremote.domain.usecase

import com.example.irremote.domain.model.IrCode
import com.example.irremote.util.IrTransmitter
import com.example.irremote.util.NecEncoder
import javax.inject.Inject

/**
 * 发送红外信号用例
 */
class SendIrSignalUseCase @Inject constructor(
    private val irTransmitter: IrTransmitter
) {
    /**
     * 发送红外信号
     * @param irCode 红外编码
     * @param frequency 载波频率（默认38000Hz）
     */
    operator fun invoke(irCode: IrCode, frequency: Int = 38000) {
        when (irCode.protocol) {
            IrCode.PROTOCOL_NEC -> {
                irTransmitter.sendNecCommand(irCode.address, irCode.command)
            }
            IrCode.PROTOCOL_RAW -> {
                irCode.rawPattern?.let { pattern ->
                    irTransmitter.sendRawPattern(pattern)
                }
            }
            else -> {
                // 默认尝试NEC协议
                irTransmitter.sendNecCommand(irCode.address, irCode.command)
            }
        }
    }

    /**
     * 检查设备是否支持红外
     */
    fun hasIrEmitter(): Boolean {
        return irTransmitter.hasIrEmitter()
    }
}
