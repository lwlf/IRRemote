package com.example.irremote.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irremote.data.local.entity.Device
import com.example.irremote.data.local.entity.Remote
import com.example.irremote.data.repository.RemoteRepository
import com.example.irremote.domain.model.RemoteButton
import com.example.irremote.domain.usecase.ImportRemoteUseCase
import com.example.irremote.domain.usecase.SendIrSignalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 遥控器界面ViewModel
 */
@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val sendIrSignalUseCase: SendIrSignalUseCase,
    private val importRemoteUseCase: ImportRemoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: 0

    private val _device = MutableStateFlow<Device?>(null)
    val device: StateFlow<Device?> = _device.asStateFlow()

    private val _remotes = MutableStateFlow<List<Remote>>(emptyList())
    val remotes: StateFlow<List<Remote>> = _remotes.asStateFlow()

    private val _currentRemote = MutableStateFlow<Remote?>(null)
    val currentRemote: StateFlow<Remote?> = _currentRemote.asStateFlow()

    private val _buttons = MutableStateFlow<List<RemoteButton>>(emptyList())
    val buttons: StateFlow<List<RemoteButton>> = _buttons.asStateFlow()

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState.asStateFlow()

    val hasIrEmitter: Boolean = sendIrSignalUseCase.hasIrEmitter()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 加载设备信息
            repository.getDeviceById(deviceId)?.let {
                _device.value = it
            }

            // 加载遥控器列表
            repository.getRemotesByDeviceId(deviceId).collect { remoteList ->
                _remotes.value = remoteList

                // 选择默认遥控器或第一个
                val defaultRemote = remoteList.find { it.isDefault } ?: remoteList.firstOrNull()
                _currentRemote.value = defaultRemote

                // 解析按键
                defaultRemote?.let { parseButtons(it) }
            }
        }
    }

    /**
     * 切换遥控器
     */
    fun switchRemote(remote: Remote) {
        _currentRemote.value = remote
        parseButtons(remote)
    }

    /**
     * 解析遥控器按键
     */
    private fun parseButtons(remote: Remote) {
        val buttons = importRemoteUseCase.parseButtons(remote.configJson)
        _buttons.value = buttons ?: emptyList()
    }

    /**
     * 发送红外信号
     */
    fun sendIrSignal(button: RemoteButton) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            try {
                sendIrSignalUseCase(button.irCode)
                _sendState.value = SendState.Success
            } catch (e: Exception) {
                _sendState.value = SendState.Error(e.message ?: "发送失败")
            }
        }
    }

    /**
     * 设置默认遥控器
     */
    fun setDefaultRemote(remoteId: Long) {
        viewModelScope.launch {
            repository.setDefaultRemote(remoteId)
        }
    }

    /**
     * 删除遥控器
     */
    fun deleteRemote(remote: Remote) {
        viewModelScope.launch {
            repository.deleteRemote(remote)
        }
    }

    sealed class SendState {
        object Idle : SendState()
        object Sending : SendState()
        object Success : SendState()
        data class Error(val message: String) : SendState()
    }
}
