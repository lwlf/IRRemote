package com.example.irremote.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 导入配置对话框
 */
@Composable
fun ImportConfigDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入遥控器配置") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "粘贴JSON配置文件内容：",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = {
                        jsonText = it
                        showError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    placeholder = {
                        Text(
                            """{
  "name": "美博空调遥控器",
  "brand": "MBO",
  "type": "ac",
  "protocol": "NEC",
  "address": "0x00FF",
  "buttons": [...]
}""",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    isError = showError
                )

                if (showError) {
                    Text(
                        "请输入有效的JSON配置",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onImport(jsonText)
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
