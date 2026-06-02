package com.example.irremote.domain.usecase

import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.local.entity.Remote
import com.example.irremote.data.repository.RemoteRepository
import com.example.irremote.domain.model.RemoteButton
import com.example.irremote.util.JsonImporter
import com.example.irremote.util.NecEncoder
import javax.inject.Inject

/**
 * 导入遥控器配置用例
 */
class ImportRemoteUseCase @Inject constructor(
    private val repository: RemoteRepository,
    private val jsonImporter: JsonImporter
) {
    /**
     * 从JSON导入遥控器配置
     * @param json JSON配置字符串
     * @param deviceId 可选：指定设备ID，不指定则创建新设备
     * @return 导入结果
     */
    suspend operator fun invoke(json: String, deviceId: Long? = null): ImportResult {
        // 验证配置
        val validation = jsonImporter.validateConfig(json)
        if (!validation.isValid) {
            return ImportResult.Error(validation.errorMessage ?: "配置无效")
        }

        val config = jsonImporter.parseConfig(json)
            ?: return ImportResult.Error("解析配置失败")

        return try {
            // 获取或创建设备
            val targetDeviceId = deviceId ?: run {
                val device = Device(
                    name = config.name,
                    brand = config.brand,
                    type = config.type,
                    iconName = getDeviceIcon(config.type)
                )
                repository.insertDevice(device)
            }

            // 创建遥控器
            val remote = Remote(
                deviceId = targetDeviceId,
                name = config.name,
                configJson = json,
                isDefault = true
            )
            val remoteId = repository.insertRemote(remote)

            ImportResult.Success(remoteId, targetDeviceId)
        } catch (e: Exception) {
            ImportResult.Error("导入失败: ${e.message}")
        }
    }

    /**
     * 将配置转换为按键列表
     */
    fun parseButtons(json: String): List<RemoteButton>? {
        val config = jsonImporter.parseConfig(json) ?: return null

        return config.buttons.map { buttonConfig ->
            val irCode = jsonImporter.buttonConfigToIrCode(
                buttonConfig,
                config.protocol,
                config.address,
                config.frequency
            )

            RemoteButton(
                id = buttonConfig.id,
                label = buttonConfig.label,
                icon = buttonConfig.icon,
                irCode = irCode,
                row = buttonConfig.row,
                col = buttonConfig.col,
                rowSpan = buttonConfig.rowSpan,
                colSpan = buttonConfig.colSpan
            )
        }
    }

    private fun getDeviceIcon(type: String): String {
        return when (type) {
            "tv" -> "tv"
            "ac" -> "ac_unit"
            "fan" -> "toys_fan"
            "stb" -> "set_top_box"
            "dvd" -> "disc_full"
            else -> "devices"
        }
    }

    sealed class ImportResult {
        data class Success(val remoteId: Long, val deviceId: Long) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }
}
