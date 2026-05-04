package com.asrafrosmadi.recipeexplorer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import com.asrafrosmadi.recipeexplorer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.MessageDigest

object ImageLoader {
    private val memoryCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun load(context: Context, url: String, imageView: ImageView, scope: CoroutineScope) {
        imageView.setImageResource(R.drawable.ic_placeholder)
        if (url.isBlank())
            return

        imageView.tag = url

        memoryCache.get(url)?.let {
            imageView.setImageBitmap(it)
            return
        }

        scope.launch {
            val bmp = withContext(Dispatchers.IO) {
                loadBitmap(context, url)
            }

            if (imageView.tag == url && bmp != null)
                imageView.setImageBitmap(bmp)
        }
    }

    private fun loadBitmap(context: Context, url: String): Bitmap? {
        val file = File(context.cacheDir, sha(url) + ".img")
        return try {
            if (file.exists())
                BitmapFactory.decodeFile(file.absolutePath)?.also {
                    memoryCache.put(url, it)
                }
            else {
                val bytes = URL(url).openStream().use {
                    it.readBytes()
                }
                file.writeBytes(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also {
                    memoryCache.put(url, it)
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun sha(input: String): String = MessageDigest.getInstance("SHA-256").digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
}
