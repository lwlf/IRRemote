package com.example.irremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.irremote.presentation.ui.brand.BrandSelectionScreen
import com.example.irremote.presentation.ui.editor.EditorScreen
import com.example.irremote.presentation.ui.home.HomeScreen
import com.example.irremote.presentation.ui.remote.RemoteScreen
import com.example.irremote.ui.theme.IRRemoteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IRRemoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // 首页
                        composable("home") {
                            HomeScreen(
                                onDeviceClick = { deviceId ->
                                    navController.navigate("remote/$deviceId")
                                },
                                onAddDevice = {
                                    navController.navigate("brand_selection")
                                }
                            )
                        }

                        // 遥控器界面
                        composable(
                            route = "remote/{deviceId}",
                            arguments = listOf(
                                navArgument("deviceId") { type = NavType.LongType }
                            )
                        ) {
                            RemoteScreen(
                                onBack = { navController.popBackStack() },
                                onEdit = { navController.navigate("editor") }
                            )
                        }

                        // 品牌选择界面
                        composable("brand_selection") {
                            BrandSelectionScreen(
                                onBack = { navController.popBackStack() },
                                onDeviceCreated = { deviceId ->
                                    navController.navigate("remote/$deviceId") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }

                        // 编辑器界面
                        composable("editor") {
                            EditorScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { json ->
                                    // TODO: 保存配置
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
