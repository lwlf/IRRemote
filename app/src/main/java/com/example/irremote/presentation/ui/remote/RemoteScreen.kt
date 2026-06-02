package com.example.irremote.presentation.ui.remote

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irremote.presentation.ui.components.IrButton
import com.example.irremote.presentation.viewmodel.RemoteViewModel

/**
 * 遥控器界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: RemoteViewModel = hiltViewModel()
) {
    val device by viewModel.device.collectAsState()
    val buttons by viewModel.buttons.collectAsState()
    val sendState by viewModel.sendState.collectAsState()
    var showSnackbar by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(device?.name ?: "遥控器") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
            )
        }
    ) { padding ->
        if (!viewModel.hasIrEmitter) {
            // 不支持红外
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "此设备不支持红外功能",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "请使用支持红外发射的设备",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (buttons.isEmpty()) {
            // 没有按键
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.TouchApp,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "没有配置按键",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "请编辑遥控器配置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // 按键网格
            val columns = 4 // TODO: 从配置读取
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(buttons) { button ->
                    IrButton(
                        button = button,
                        onClick = {
                            viewModel.sendIrSignal(button)
                        },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .then(
                                if (button.rowSpan > 1 || button.colSpan > 1) {
                                    Modifier.fillMaxWidth()
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    }

    // 发送状态提示
    when (val state = sendState) {
        is RemoteViewModel.SendState.Error -> {
            LaunchedEffect(state) {
                showSnackbar = state.message
            }
        }
        else -> {}
    }

    // Snackbar
    showSnackbar?.let { message ->
        LaunchedEffect(message) {
            // TODO: 显示Snackbar
            showSnackbar = null
        }
    }
}
