package com.example.irremote.util

import com.example.irremote.domain.model.ButtonConfig
import com.example.irremote.domain.model.IrCode
import com.example.irremote.domain.model.RemoteConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * JSON配置导入工具
 */
@Singleton
class JsonImporter @Inject constructor(
    private val gson: Gson
) {
    /**
     * 从JSON字符串解析遥控器配置
     */
    fun parseConfig(json: String): RemoteConfig? {
        return try {
            gson.fromJson(json, RemoteConfig::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将ButtonConfig转换为IrCode
     */
    fun buttonConfigToIrCode(
        buttonConfig: ButtonConfig,
        defaultProtocol: String,
        defaultAddress: String,
        defaultFrequency: Int
    ): IrCode {
        val protocol = buttonConfig.protocol ?: defaultProtocol
        val address = NecEncoder.parseHexString(buttonConfig.address ?: defaultAddress)
        val command = NecEncoder.parseHexString(buttonConfig.command)

        return when (protocol) {
            IrCode.PROTOCOL_RAW -> {
                val rawPattern = buttonConfig.rawPattern?.toIntArray()
                IrCode(
                    protocol = protocol,
                    address = 0,
                    command = 0,
                    rawPattern = rawPattern
                )
            }
            else -> {
                IrCode(
                    protocol = protocol,
                    address = address,
                    command = command
                )
            }
        }
    }

    /**
     * 验证JSON格式是否有效
     */
    fun validateConfig(json: String): ValidationResult {
        val config = parseConfig(json) ?: return ValidationResult(false, "JSON格式无效")

        if (config.name.isBlank()) {
            return ValidationResult(false, "遥控器名称不能为空")
        }

        if (config.type.isBlank()) {
            return ValidationResult(false, "设备类型不能为空")
        }

        if (config.buttons.isEmpty()) {
            return ValidationResult(false, "至少需要一个按键")
        }

        return ValidationResult(true, null)
    }

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String?
    )
}
