package com.example.irremote.presentation.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 遥控器编辑器界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ac") }
    var protocol by remember { mutableStateOf("NEC") }
    var address by remember { mutableStateOf("0x00FF") }
    var jsonConfig by remember { mutableStateOf("") }
    var showJsonEditor by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑遥控器") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { onSave(jsonConfig) }) {
                        Icon(Icons.Default.Save, contentDescription = "保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 基本信息
            Text(
                "基本信息",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("遥控器名称") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("品牌（可选）") },
                modifier = Modifier.fillMaxWidth()
            )

            // 设备类型选择
            Text(
                "设备类型",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == "ac",
                    onClick = { type = "ac" },
                    label = { Text("空调") }
                )
                FilterChip(
                    selected = type == "tv",
                    onClick = { type = "tv" },
                    label = { Text("电视") }
                )
                FilterChip(
                    selected = type == "fan",
                    onClick = { type = "fan" },
                    label = { Text("风扇") }
                )
                FilterChip(
                    selected = type == "other",
                    onClick = { type = "other" },
                    label = { Text("其他") }
                )
            }

            HorizontalDivider()

            // 协议设置
            Text(
                "协议设置",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = protocol,
                onValueChange = { protocol = it },
                label = { Text("协议类型") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("NEC, RC5, RC6, RAW") }
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("设备地址") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("十六进制，如 0x00FF") }
            )

            HorizontalDivider()

            // JSON编辑器
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "JSON配置",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { showJsonEditor = !showJsonEditor }) {
                    Text(if (showJsonEditor) "收起" else "展开")
                }
            }

            if (showJsonEditor) {
                OutlinedTextField(
                    value = jsonConfig,
                    onValueChange = { jsonConfig = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 300.dp),
                    placeholder = {
                        Text("粘贴完整的JSON配置...")
                    }
                )
            }

            // 示例配置按钮
            Button(
                onClick = {
                    jsonConfig = getExampleConfig(name, brand, type, protocol, address)
                    showJsonEditor = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Code, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("生成示例配置")
            }
        }
    }
}

private fun getExampleConfig(
    name: String,
    brand: String,
    type: String,
    protocol: String,
    address: String
): String {
    return """
{
  "name": "${name.ifBlank { "我的遥控器" }}",
  "brand": "${brand.ifBlank { "" }}",
  "type": "$type",
  "protocol": "$protocol",
  "address": "$address",
  "frequency": 38000,
  "gridColumns": 4,
  "buttons": [
    {
      "id": "power",
      "label": "电源",
      "icon": "power",
      "command": "0x45",
      "row": 0,
      "col": 0
    },
    {
      "id": "temp_up",
      "label": "温度+",
      "icon": "arrow_up",
      "command": "0x46",
      "row": 0,
      "col": 1
    },
    {
      "id": "temp_down",
      "label": "温度-",
      "icon": "arrow_down",
      "command": "0x47",
      "row": 0,
      "col": 2
    },
    {
      "id": "mode",
      "label": "模式",
      "icon": "mode",
      "command": "0x44",
      "row": 0,
      "col": 3
    },
    {
      "id": "fan",
      "label": "风速",
      "icon": "fan",
      "command": "0x40",
      "row": 1,
      "col": 0
    },
    {
      "id": "swing",
      "label": "摆风",
      "icon": "swing",
      "command": "0x1A",
      "row": 1,
      "col": 1
    },
    {
      "id": "timer",
      "label": "定时",
      "icon": "timer",
      "command": "0x12",
      "row": 1,
      "col": 2
    }
  ]
}
""".trimIndent()
}
