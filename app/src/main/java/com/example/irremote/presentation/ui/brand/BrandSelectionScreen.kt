package com.example.irremote.presentation.ui.brand

import androidx.compose.foundation.clickable
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
import com.example.irremote.presentation.viewmodel.BrandSelectionViewModel
import com.example.irremote.util.BrandLibrary

/**
 * 品牌选择界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandSelectionScreen(
    onBack: () -> Unit,
    onDeviceCreated: (Long) -> Unit,
    viewModel: BrandSelectionViewModel = hiltViewModel()
) {
    val brands by viewModel.brands.collectAsState()
    val models by viewModel.models.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val importState by viewModel.importState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (selectedBrand != null) "${selectedBrand!!.name} - 选择型号" else "选择品牌")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedBrand != null) {
                            viewModel.clearBrandSelection()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 搜索框
            if (selectedBrand == null) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchBrands(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("搜索品牌...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchBrands("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                        }
                    },
                    singleLine = true
                )
            }

            // 内容列表
            if (selectedBrand == null) {
                // 品牌列表
                BrandList(
                    brands = brands,
                    onBrandClick = { viewModel.selectBrand(it) }
                )
            } else {
                // 型号列表
                ModelList(
                    models = models,
                    onModelClick = { viewModel.importModel(it.id) }
                )
            }
        }
    }

    // 导入状态处理
    when (val state = importState) {
        is BrandSelectionViewModel.ImportState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BrandSelectionViewModel.ImportState.Success -> {
            LaunchedEffect(state) {
                onDeviceCreated(state.deviceId)
                viewModel.resetImportState()
            }
        }
        is BrandSelectionViewModel.ImportState.Error -> {
            LaunchedEffect(state) {
                // TODO: 显示错误提示
                viewModel.resetImportState()
            }
        }
        else -> {}
    }
}

@Composable
private fun BrandList(
    brands: List<BrandLibrary.BrandInfo>,
    onBrandClick: (BrandLibrary.BrandInfo) -> Unit
) {
    if (brands.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "没有找到品牌",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(brands) { brand ->
                BrandItem(
                    brand = brand,
                    onClick = { onBrandClick(brand) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrandItem(
    brand: BrandLibrary.BrandInfo,
    onClick: () -> Unit
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
                imageVector = Icons.Default.AcUnit,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = brand.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = brand.nameEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModelList(
    models: List<BrandLibrary.ModelInfo>,
    onModelClick: (BrandLibrary.ModelInfo) -> Unit
) {
    if (models.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "没有可用的型号",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(models) { model ->
                ModelItem(
                    model = model,
                    onClick = { onModelClick(model) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelItem(
    model: BrandLibrary.ModelInfo,
    onClick: () -> Unit
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
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = model.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
