package com.example.irremote.domain.usecase

import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.local.entity.Remote
import com.example.irremote.data.repository.RemoteRepository
import com.example.irremote.domain.model.RemoteButton
import com.example.irremote.domain.model.RemoteConfig
import com.example.irremote.util.BrandLibrary
import com.example.irremote.util.JsonImporter
import com.google.gson.Gson
import javax.inject.Inject

/**
 * 从品牌库导入配置用例
 */
class ImportFromBrandLibraryUseCase @Inject constructor(
    private val repository: RemoteRepository,
    private val brandLibrary: BrandLibrary,
    private val jsonImporter: JsonImporter,
    private val gson: Gson
) {
    /**
     * 从品牌库导入遥控器配置
     * @param brandId 品牌ID
     * @param modelId 型号ID
     * @return 导入结果
     */
    suspend operator fun invoke(brandId: String, modelId: String): ImportResult {
        // 获取配置
        val config = brandLibrary.getRemoteConfig(brandId, modelId)
            ?: return ImportResult.Error("找不到指定的品牌或型号")

        return try {
            // 创建设备
            val device = Device(
                name = config.name,
                brand = config.brand,
                type = config.type,
                iconName = getDeviceIcon(config.type)
            )
            val deviceId = repository.insertDevice(device)

            // 创建遥控器
            val configJson = gson.toJson(config)
            val remote = Remote(
                deviceId = deviceId,
                name = config.name,
                configJson = configJson,
                isDefault = true
            )
            val remoteId = repository.insertRemote(remote)

            ImportResult.Success(remoteId, deviceId)
        } catch (e: Exception) {
            ImportResult.Error("导入失败: ${e.message}")
        }
    }

    /**
     * 将配置转换为按键列表
     */
    fun parseButtons(config: RemoteConfig): List<RemoteButton> {
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
