package com.example.irremote.util

import android.content.Context
import com.example.irremote.domain.model.RemoteConfig
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 品牌编码库管理器
 */
@Singleton
class BrandLibrary @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    // 品牌配置缓存
    private var brandCache: Map<String, BrandData>? = null

    /**
     * 获取所有品牌列表
     */
    fun getAllBrands(): List<BrandInfo> {
        return loadBrandFiles().map { file ->
            val brandData = loadBrandData(file)
            BrandInfo(
                id = file.removeSuffix(".json"),
                name = brandData?.brand ?: file,
                nameEn = brandData?.brandEn ?: file,
                type = brandData?.type ?: "ac"
            )
        }
    }

    /**
     * 根据品牌ID获取品牌数据
     */
    fun getBrandData(brandId: String): BrandData? {
        return loadBrandData("$brandId.json")
    }

    /**
     * 获取品牌的可用型号列表
     */
    fun getBrandModels(brandId: String): List<ModelInfo> {
        val brandData = getBrandData(brandId) ?: return emptyList()
        return brandData.models.map { model ->
            ModelInfo(
                id = model.code,
                name = model.name,
                description = model.description
            )
        }
    }

    /**
     * 获取指定品牌和型号的遥控器配置
     */
    fun getRemoteConfig(brandId: String, modelId: String): RemoteConfig? {
        val brandData = getBrandData(brandId) ?: return null
        val model = brandData.models.find { it.code == modelId } ?: return null
        return model.config
    }

    /**
     * 搜索品牌
     */
    fun searchBrands(query: String): List<BrandInfo> {
        val lowerQuery = query.lowercase()
        return getAllBrands().filter { brand ->
            brand.name.lowercase().contains(lowerQuery) ||
            brand.nameEn.lowercase().contains(lowerQuery)
        }
    }

    /**
     * 加载品牌文件列表
     */
    private fun loadBrandFiles(): List<String> {
        return try {
            context.assets.list("brands")?.toList() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载品牌数据
     */
    private fun loadBrandData(fileName: String): BrandData? {
        return try {
            val json = context.assets.open("brands/$fileName").bufferedReader().use { it.readText() }
            gson.fromJson(json, BrandData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 品牌数据模型
     */
    data class BrandData(
        val brand: String,
        val brandEn: String,
        val type: String,
        val protocol: String,
        val address: String,
        val frequency: Int,
        val models: List<ModelData>
    )

    /**
     * 型号数据模型
     */
    data class ModelData(
        val name: String,
        val code: String,
        val description: String,
        val config: RemoteConfig
    )

    /**
     * 品牌信息
     */
    data class BrandInfo(
        val id: String,
        val name: String,
        val nameEn: String,
        val type: String
    )

    /**
     * 型号信息
     */
    data class ModelInfo(
        val id: String,
        val name: String,
        val description: String
    )
}
