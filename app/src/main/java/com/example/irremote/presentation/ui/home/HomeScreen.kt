package com.example.irremote.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irremote.data.local.entity.Device
import com.example.irremote.presentation.viewmodel.HomeViewModel

/**
 * 首页 - 设备列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDeviceClick: (Long) -> Unit,
    onAddDevice: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val importState by viewModel.importState.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Device?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("红外遥控器") },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("从品牌库添加") },
                                onClick = {
                                    showMenu = false
                                    onAddDevice()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.BrandingWatermark, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("导入JSON配置") },
                                onClick = {
                                    showMenu = false
                                    showImportDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.FileOpen, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddDevice) {
                Icon(Icons.Default.Add, contentDescription = "添加设备")
            }
        }
    ) { padding ->
        if (devices.isEmpty()) {
            // 空状态
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
                        Icons.Default.DevicesOther,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "还没有设备",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "点击右下角添加设备，或从菜单选择品牌",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // 设备列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(devices) { device ->
                    DeviceCard(
                        device = device,
                        onClick = { onDeviceClick(device.id) },
                        onDelete = { showDeleteDialog = device }
                    )
                }
            }
        }
    }

    // 导入配置对话框
    if (showImportDialog) {
        ImportConfigDialog(
            onDismiss = { showImportDialog = false },
            onImport = { json ->
                viewModel.importConfig(json)
                showImportDialog = false
            }
        )
    }

    // 删除确认对话框
    showDeleteDialog?.let { device ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除设备") },
            text = { Text("确定要删除 ${device.name} 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDevice(device)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }

    // 导入状态提示
    when (val state = importState) {
        is HomeViewModel.ImportState.Error -> {
            LaunchedEffect(state) {
                // TODO: 显示错误提示
                viewModel.resetImportState()
            }
        }
        is HomeViewModel.ImportState.Success -> {
            LaunchedEffect(state) {
                onDeviceClick(state.deviceId)
                viewModel.resetImportState()
            }
        }
        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getDeviceIcon(device.type),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (device.brand != null) {
                    Text(
                        text = device.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun getDeviceIcon(type: String) = when (type) {
    "tv" -> Icons.Default.Tv
    "ac" -> Icons.Default.AcUnit
    "fan" -> Icons.Default.Toys
    "stb" -> Icons.Default.SettingsInputHdmi
    else -> Icons.Default.DevicesOther
}
