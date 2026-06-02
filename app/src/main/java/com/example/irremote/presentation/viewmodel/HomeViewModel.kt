package com.example.irremote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.repository.RemoteRepository
import com.example.irremote.domain.usecase.ImportRemoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val importRemoteUseCase: ImportRemoteUseCase
) : ViewModel() {

    val devices: StateFlow<List<Device>> = repository.getAllDevices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    /**
     * 导入遥控器配置
     */
    fun importConfig(json: String) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading

            when (val result = importRemoteUseCase(json)) {
                is ImportRemoteUseCase.ImportResult.Success -> {
                    _importState.value = ImportState.Success(result.deviceId)
                }
                is ImportRemoteUseCase.ImportResult.Error -> {
                    _importState.value = ImportState.Error(result.message)
                }
            }
        }
    }

    /**
     * 删除设备
     */
    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            repository.deleteDevice(device)
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
