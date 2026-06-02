package com.example.irremote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irremote.domain.usecase.ImportFromBrandLibraryUseCase
import com.example.irremote.util.BrandLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 品牌选择ViewModel
 */
@HiltViewModel
class BrandSelectionViewModel @Inject constructor(
    private val brandLibrary: BrandLibrary,
    private val importFromBrandLibraryUseCase: ImportFromBrandLibraryUseCase
) : ViewModel() {

    private val _brands = MutableStateFlow<List<BrandLibrary.BrandInfo>>(emptyList())
    val brands: StateFlow<List<BrandLibrary.BrandInfo>> = _brands.asStateFlow()

    private val _models = MutableStateFlow<List<BrandLibrary.ModelInfo>>(emptyList())
    val models: StateFlow<List<BrandLibrary.ModelInfo>> = _models.asStateFlow()

    private val _selectedBrand = MutableStateFlow<BrandLibrary.BrandInfo?>(null)
    val selectedBrand: StateFlow<BrandLibrary.BrandInfo?> = _selectedBrand.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    init {
        loadBrands()
    }

    /**
     * 加载品牌列表
     */
    private fun loadBrands() {
        _brands.value = brandLibrary.getAllBrands()
    }

    /**
     * 搜索品牌
     */
    fun searchBrands(query: String) {
        _searchQuery.value = query
        _brands.value = if (query.isBlank()) {
            brandLibrary.getAllBrands()
        } else {
            brandLibrary.searchBrands(query)
        }
    }

    /**
     * 选择品牌
     */
    fun selectBrand(brand: BrandLibrary.BrandInfo) {
        _selectedBrand.value = brand
        _models.value = brandLibrary.getBrandModels(brand.id)
    }

    /**
     * 清除品牌选择
     */
    fun clearBrandSelection() {
        _selectedBrand.value = null
        _models.value = emptyList()
    }

    /**
     * 导入指定型号
     */
    fun importModel(modelId: String) {
        val brand = _selectedBrand.value ?: return

        viewModelScope.launch {
            _importState.value = ImportState.Loading

            when (val result = importFromBrandLibraryUseCase(brand.id, modelId)) {
                is ImportFromBrandLibraryUseCase.ImportResult.Success -> {
                    _importState.value = ImportState.Success(result.deviceId)
                }
                is ImportFromBrandLibraryUseCase.ImportResult.Error -> {
                    _importState.value = ImportState.Error(result.message)
                }
            }
        }
    }

    /**
     * 重置导入状态
     */
    fun resetImportState() {
        _importState.value = ImportState.Idle
    }

    sealed class ImportState {
        object Idle : ImportState()
        object Loading : ImportState()
        data class Success(val deviceId: Long) : ImportState()
        data class Error(val message: String) : ImportState()
    }
}
