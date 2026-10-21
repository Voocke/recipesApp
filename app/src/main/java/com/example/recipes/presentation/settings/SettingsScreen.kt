package com.example.recipes.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipes.ui.components.CraftBottomBar
import com.example.recipes.ui.components.CraftNavItem
import com.example.recipes.ui.components.DashedCard
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.SageGreen
import com.example.recipes.ui.theme.Terracotta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Launcher for exporting recipes to a JSON file
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportToFile(contentResolver, uri)
        }
    }

    // Launcher for selecting a JSON file to import
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importFromFile(contentResolver, uri)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PaperCream,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = InkDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = InkDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperCream
                )
            )
        },
        bottomBar = {
            CraftBottomBar(
                currentRoute = CraftNavItem.Settings.route,
                onNavigate = { route ->
                    if (route == CraftNavItem.AllRecipes.route || route == CraftNavItem.Recent.route) {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header Card
            item {
                DashedCard(
                    backgroundColor = BakedMilk,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Кулинарная книга",
                                fontFamily = HandwrittenFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = InkDark
                            )
                            Text(
                                text = "Всего сохранено рецептов: ${uiState.recipeCount}",
                                fontFamily = HandwrittenFamily,
                                fontSize = 17.sp,
                                color = InkWashed
                            )
                        }
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Terracotta,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            // Section 1: JSON Export & Import
            item {
                Text(
                    text = "Экспорт и импорт рецептов",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = InkDark,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            // Export Card
            item {
                DashedCard(
                    backgroundColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Экспорт данных (JSON)",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = InkDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Выгрузите все рецепты, ингредиенты и шаги в стандартный файл JSON для резервной копии или переноса на другое устройство.",
                        fontFamily = HandwrittenFamily,
                        fontSize = 16.sp,
                        color = InkWashed
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CraftActionButton(
                            text = "Сохранить в файл",
                            icon = Icons.Default.Download,
                            containerColor = Terracotta,
                            contentColor = Color.White,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                createDocumentLauncher.launch("recipes_backup_$dateStr.json")
                            }
                        )

                        CraftActionButton(
                            text = "Поделиться",
                            icon = Icons.Default.Share,
                            containerColor = BakedMilk,
                            contentColor = InkDark,
                            borderColor = DottedBorderColor,
                            onClick = {
                                viewModel.shareRecipes(context)
                            }
                        )
                    }
                }
            }

            // Import Card
            item {
                DashedCard(
                    backgroundColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Импорт данных (JSON)",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = InkDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Загрузите рецепты из JSON-файла. Поддерживается файл бэкапа со всеми ингредиентами, шагами и категориями.",
                        fontFamily = HandwrittenFamily,
                        fontSize = 16.sp,
                        color = InkWashed
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CraftActionButton(
                        text = "Выбрать JSON-файл для импорта",
                        icon = Icons.Default.Upload,
                        containerColor = SageGreen,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            openDocumentLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                        }
                    )
                }
            }

            // Section 2: Future sync services teaser ("потом привяжем другие сервисы")
            item {
                Text(
                    text = "Синхронизация",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = InkDark,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                DashedCard(
                    backgroundColor = BakedMilk.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SageGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Облачные сервисы",
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = InkDark
                                )
                                Text(
                                    text = "Google Drive, Telegram, WebDAV",
                                    fontFamily = HandwrittenFamily,
                                    fontSize = 15.sp,
                                    color = InkWashed
                                )
                            }
                        }

                        // Badge "Скоро"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Terracotta.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Скоро",
                                fontFamily = HandwrittenFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Terracotta
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable craft action button with optional leading icon and dashed/solid border.
 */
@Composable
private fun CraftActionButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color? = null
) {
    val shape = RoundedCornerShape(10.dp)
    val borderModifier = if (borderColor != null) {
        Modifier.border(1.dp, borderColor, shape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor, shape)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor
            )
        }
    }
}
