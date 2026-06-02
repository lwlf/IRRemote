package com.example.irremote.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irremote.domain.model.RemoteButton

/**
 * 红外遥控器按键组件
 */
@Composable
fun IrButton(
    button: RemoteButton,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (button.icon != null) {
                Icon(
                    imageVector = getIcon(button.icon),
                    contentDescription = button.label,
                    modifier = Modifier.fillMaxSize(0.6f)
                )
            } else {
                Text(
                    text = button.label,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 根据名称获取图标
 */
private fun getIcon(iconName: String): ImageVector {
    return when (iconName) {
        "power" -> Icons.Default.PowerSettingsNew
        "arrow_up" -> Icons.Default.KeyboardArrowUp
        "arrow_down" -> Icons.Default.KeyboardArrowDown
        "arrow_left" -> Icons.Default.KeyboardArrowLeft
        "arrow_right" -> Icons.Default.KeyboardArrowRight
        "ok", "confirm" -> Icons.Default.Check
        "menu" -> Icons.Default.Menu
        "back", "return" -> Icons.Default.ArrowBack
        "home" -> Icons.Default.Home
        "volume_up" -> Icons.Default.VolumeUp
        "volume_down" -> Icons.Default.VolumeDown
        "volume_mute" -> Icons.Default.VolumeOff
        "channel_up" -> Icons.Default.Add
        "channel_down" -> Icons.Default.Remove
        "mute" -> Icons.Default.VolumeOff
        "settings" -> Icons.Default.Settings
        "input" -> Icons.Default.Input
        "play" -> Icons.Default.PlayArrow
        "pause" -> Icons.Default.Pause
        "stop" -> Icons.Default.Stop
        "forward" -> Icons.Default.SkipNext
        "backward" -> Icons.Default.SkipPrevious
        "ac_unit" -> Icons.Default.AcUnit
        "mode" -> Icons.Default.Tune
        "fan" -> Icons.Default.Toys
        "swing" -> Icons.Default.SwapVert
        "timer" -> Icons.Default.Timer
        else -> Icons.Default.TouchApp
    }
}
