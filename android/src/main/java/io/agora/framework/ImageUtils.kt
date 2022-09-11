package io.agora.framework

import android.content.Intent
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import kotlin.math.roundToInt
import top.zibin.luban.Luban

// 图片实用类
object ImageUtils {
    fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val image = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()
        } catch (e: Exception) {
            Log.e("ImageUtils","nv21ToBitmap 失败", e)
        }
        return bitmap
    }

    fun saveBitmap(context: Context, filepath: String, bitmap: Bitmap): File? {
        val file = File(filepath)
        if (file.exists()) {
            file.delete()
        }
        var fos: FileOutputStream? = null
        return try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            val files = Luban.with(context).load(file).get()
            return if (files.isEmpty()) {
                file.delete()
                null
            } else {
                if (files[0].absolutePath != file.absolutePath) {
                    file.delete()
                }
                files[0]
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "保存图片失败", e)
            null
        } finally {
            fos?.close()
        }
    }
}