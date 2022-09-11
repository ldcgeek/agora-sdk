package io.agora.framework

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES20
import android.util.Log
import com.faceunity.FURenderer
import io.agora.capture.framework.modules.channels.VideoChannel.ChannelContext
import io.agora.capture.framework.modules.processors.IPreprocessor
import io.agora.capture.video.camera.VideoCaptureFrame
import java.io.File
import kotlinx.coroutines.*

class PreprocessorFaceUnity(private val context: Context, private val mFURenderer: FURenderer?, private val picListener: ((file: File) -> Unit)? = null) : IPreprocessor {
    @Volatile
    private var needPic = false
    private var picFilepath: String? = null

    @Volatile
    private var takingPic = false
    fun takePicture(filepath: String) {
        if (takingPic) {
            return
        }
        needPic = true
        picFilepath = filepath
    }

    private var mEnabled = true
    override fun onPreProcessFrame(outFrame: VideoCaptureFrame, context: ChannelContext): VideoCaptureFrame {
        if (needPic) {
            takePicture(outFrame.image, outFrame.format.width, outFrame.format.height)
        }

        if (!mEnabled) {
            Log.d("RtcEngine", "没有美颜 ${this}")
        }

        if (mFURenderer == null || !mEnabled) {
            return outFrame
        }

        outFrame.textureId = mFURenderer.onDrawFrame(outFrame.image, outFrame.textureId, outFrame.format.width, outFrame.format.height)

        // The texture is transformed to texture2D by beauty module.
        outFrame.format.texFormat = GLES20.GL_TEXTURE_2D
        return outFrame
    }

    override fun initPreprocessor() {
        mFURenderer?.onSurfaceCreated()
    }

    override fun enablePreProcess(enabled: Boolean) {
        mEnabled = enabled
    }

    override fun releasePreprocessor(context: ChannelContext) {
        Log.d(TAG, "releasePreprocessor: ")
    }

    fun takePicture(bytes: ByteArray, width: Int, height: Int) {
        needPic = false
        takingPic = true
        CoroutineScope(GlobalScope.coroutineContext + Dispatchers.IO).launch {
            try {
                val bitmap = ImageUtils.nv21ToBitmap(bytes, width, height)
                if (bitmap == null) {
                    Log.e(TAG,  "保存图片失败 nv21ToBitmap")
                    return@launch
                }
                val matrix = Matrix()
                matrix.postRotate(-90f)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
                bitmap.recycle()
                val file = ImageUtils.saveBitmap(context, picFilepath!!, rotatedBitmap)
                if (file == null) {
                    Log.e(TAG, "保存图片失败 saveBitmap")
                    rotatedBitmap.recycle()
                    return@launch
                }
                rotatedBitmap.recycle()
                Log.d(TAG, "图片保存: ${file.absolutePath}")
                withContext(Dispatchers.Main) {
                    picListener?.invoke(file)
                }
                takingPic = false
            } catch (e: Exception) {
                takingPic = false
                needPic = true
                Log.e(TAG, "保存图片失败", e)
            }
        }
    }

    companion object {
        private val TAG = PreprocessorFaceUnity::class.java.simpleName
    }
}