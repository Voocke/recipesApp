package com.example.recipes.presentation.settings

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.data.repository.RecipeRepository
import com.example.recipes.data.sync.RecipeBackupService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val backupService: RecipeBackupService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private val _isError = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.getRecentlyAdded(),
        _isLoading,
        _message,
        _isError
    ) { recipes, loading, message, isError ->
        SettingsUiState(
            recipeCount = recipes.size,
            isLoading = loading,
            message = message,
            isError = isError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun clearMessage() {
        _message.value = null
        _isError.value = false
    }

    /**
     * Exports recipes to the selected file URI via ContentResolver.
     */
    fun exportToFile(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val json = withContext(Dispatchers.IO) {
                    backupService.exportRecipesToJson()
                }
                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("Не удалось открыть файл для записи")
                }
                _isError.value = false
                _message.value = "Рецепты успешно экспортированы в JSON!"
            } catch (e: Exception) {
                _isError.value = true
                _message.value = "Ошибка при экспорте: ${e.localizedMessage ?: "неизвестная ошибка"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Imports recipes from a chosen file URI.
     */
    fun importFromFile(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("Не удалось прочитать выбранный файл")
                }
                val result = withContext(Dispatchers.IO) {
                    backupService.importRecipesFromJson(jsonString)
                }
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    _isError.value = false
                    _message.value = "Успешно импортировано рецептов: $count"
                } else {
                    _isError.value = true
                    _message.value = "Ошибка импорта: ${result.exceptionOrNull()?.localizedMessage ?: "неверный формат JSON"}"
                }
            } catch (e: Exception) {
                _isError.value = true
                _message.value = "Ошибка при чтении файла: ${e.localizedMessage ?: "неизвестная ошибка"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Shares JSON recipes through Android Share sheet.
     */
    fun shareRecipes(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val json = withContext(Dispatchers.IO) {
                    backupService.exportRecipesToJson()
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Резервная копия рецептов JSON")
                    putExtra(Intent.EXTRA_TEXT, json)
                }
                val chooser = Intent.createChooser(intent, "Поделиться рецептами (JSON)")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (e: Exception) {
                _isError.value = true
                _message.value = "Ошибка при создании экспорта: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
