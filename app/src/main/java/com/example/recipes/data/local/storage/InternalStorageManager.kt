package com.example.recipes.data.local.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Copies and compresses an image from the provided [uri] to
     * `context.filesDir/recipe_images/{UUID}.jpg` with JPEG format at 85% quality.
     *
     * @param uri The URI of the image (e.g. selected from gallery).
     * @return The absolute file path of the saved image for storing in Room.
     */
    suspend fun saveImageFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        saveImageFromUri(context, uri)
    }

    /**
     * Deletes an image file by its absolute path.
     */
    fun deleteImage(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val DIRECTORY_NAME = "recipe_images"
        const val JPEG_QUALITY = 85

        suspend fun saveImageFromUri(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
            val directory = File(context.filesDir, DIRECTORY_NAME)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(directory, fileName)

            // Determine image dimensions to downscale huge camera pictures
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, boundsOptions)
            }
            val maxDim = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
            var sampleSize = 1
            while (maxDim / sampleSize > 1280) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            } ?: throw IOException("Could not open input stream or decode bitmap for Uri: $uri")

            try {
                FileOutputStream(file).use { outputStream ->
                    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                    if (!success) {
                        throw IOException("Failed to compress bitmap to JPEG format")
                    }
                }
            } finally {
                bitmap.recycle()
            }

            file.absolutePath
        }
    }
}
