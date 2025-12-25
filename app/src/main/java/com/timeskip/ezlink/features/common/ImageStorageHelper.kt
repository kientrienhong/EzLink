package com.timeskip.ezlink.features.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.graphics.scale
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

object ImageStorageHelper {
    private const val TAG = "ImageStorageHelper"
    private const val PERMANENT_IMAGE_DIRECTORY = "links_images"
    private const val CACHE_IMAGE_DIRECTORY = "shared_images_cache"
    private const val MAX_IMAGE_SIZE = 1024 // Max width/height in pixels
    private const val COMPRESSION_QUALITY = 80 // JPEG quality (0-100)

    /**
     * Compress and store an image from URI to cache directory (temporary storage)
     * @param context Application context
     * @param imageUri Source image URI
     * @return File URI of the cached image, or null if failed
     */
    suspend fun compressAndStoreImageInCache(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            // Read the image from URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { stream ->
                // Decode the bitmap
                val originalBitmap = BitmapFactory.decodeStream(stream)
                    ?: return@withContext null

                // Compress the bitmap
                val compressedBitmap = compressBitmap(originalBitmap)

                // Save to cache directory
                val savedFile = saveBitmapToDirectory(context, compressedBitmap, CACHE_IMAGE_DIRECTORY)

                // Recycle bitmaps to free memory
                if (originalBitmap != compressedBitmap) {
                    originalBitmap.recycle()
                }
                compressedBitmap.recycle()

                return@withContext savedFile?.path
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error compressing and storing image to cache", e)
            null
        }
    }

    /**
     * Move a cached image to permanent storage
     * @param context Application context
     * @param cacheImageUri URI of the cached image
     * @return File URI of the permanent image, or null if failed
     */
    suspend fun moveCacheImageToPermanent(context: Context, cacheImageUri: String): String? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(cacheImageUri.toUri().path ?: return@withContext null)
            if (!cacheFile.exists()) {
                Log.w(TAG, "Cache image file does not exist: $cacheImageUri")
                return@withContext null
            }

            // Read the cached image
            val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath) ?: return@withContext null

            // Save to permanent directory
            val permanentFile = saveBitmapToDirectory(context, bitmap, PERMANENT_IMAGE_DIRECTORY)
            bitmap.recycle()

            // Delete the cache file
            if (cacheFile.delete()) {
                Log.d(TAG, "Cache file deleted: ${cacheFile.absolutePath}")
            }

            return@withContext permanentFile?.let { Uri.fromFile(it).toString() }
        } catch (e: Exception) {
            Log.e(TAG, "Error moving cache image to permanent storage", e)
            null
        }
    }

    /**
     * Compress and store an image from URI to internal storage
     * @param context Application context
     * @param imageUri Source image URI
     * @return File URI of the stored image, or null if failed
     */
    @Suppress("unused")
    suspend fun compressAndStoreImage(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            // Read the image from URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { stream ->
                // Decode the bitmap
                val originalBitmap = BitmapFactory.decodeStream(stream)
                    ?: return@withContext null

                // Compress the bitmap
                val compressedBitmap = compressBitmap(originalBitmap)

                // Save to permanent storage
                val savedFile = saveBitmapToDirectory(context, compressedBitmap, PERMANENT_IMAGE_DIRECTORY)

                // Recycle bitmaps to free memory
                if (originalBitmap != compressedBitmap) {
                    originalBitmap.recycle()
                }
                compressedBitmap.recycle()

                return@withContext savedFile?.let { Uri.fromFile(it).toString() }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error compressing and storing image", e)
            null
        }
    }

    /**
     * Compress bitmap by scaling down if necessary
     */
    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Check if resize is needed
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }

        // Calculate scale factor
        val scaleFactor = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }

        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()

        return bitmap.scale(newWidth, newHeight)
    }

    /**
     * Save bitmap to a specific directory
     */
    private fun saveBitmapToDirectory(context: Context, bitmap: Bitmap, directoryName: String): File? {
        try {
            // Create directory if it doesn't exist
            val directory = File(context.filesDir, directoryName)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Create unique filename
            val filename = "${UUID.randomUUID()}.jpg"
            val file = File(directory, filename)

            // Write bitmap to file
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
                outputStream.flush()
            }

            Log.d(TAG, "Image saved to: ${file.absolutePath}")
            return file
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap to directory $directoryName", e)
            return null
        }
    }

    /**
     * Delete an image from internal storage
     */
    suspend fun deleteImage(@Suppress("UNUSED_PARAMETER") context: Context, fileUri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = fileUri.toUri()
            val file = File(uri.path ?: return@withContext false)

            if (file.exists() && file.delete()) {
                Log.d(TAG, "Image deleted: ${file.absolutePath}")
                return@withContext true
            }
            false
        } catch (_: Exception) {
            Log.e(TAG, "Error deleting image")
            false
        }
    }

    /**
     * Check if URI is a local file stored by this app
     */
    fun isLocalStoredImage(context: Context, uri: String): Boolean {
        return try {
            val parsedUri = uri.toUri()
            val file = File(parsedUri.path ?: return false)
            val internalPath = context.filesDir.absolutePath
            file.absolutePath.contains(internalPath) &&
            (file.absolutePath.contains(PERMANENT_IMAGE_DIRECTORY) ||
             file.absolutePath.contains(CACHE_IMAGE_DIRECTORY))
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Check if a URL string represents an image
     */
    fun isImageUrl(url: String): Boolean {
        val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg")
        val lowerUrl = url.lowercase()
        return imageExtensions.any { lowerUrl.contains(it) }
    }

    /**
     * Clear all cached images
     */
    suspend fun clearCacheImages(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val cacheDirectory = File(context.filesDir, CACHE_IMAGE_DIRECTORY)
            if (!cacheDirectory.exists()) {
                Log.d(TAG, "Cache directory does not exist")
                return@withContext true
            }

            val files = cacheDirectory.listFiles()
            var allDeleted = true
            files?.forEach { file ->
                if (!file.delete()) {
                    Log.w(TAG, "Failed to delete cache file: ${file.absolutePath}")
                    allDeleted = false
                }
            }

            if (allDeleted && files?.isNotEmpty() == true) {
                Log.d(TAG, "Cache directory cleared: ${cacheDirectory.absolutePath}")
            }
            return@withContext allDeleted
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache images", e)
            false
        }
    }

}

