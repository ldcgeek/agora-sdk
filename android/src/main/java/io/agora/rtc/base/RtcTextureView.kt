package io.agora.rtc.base

import android.content.Context
import android.util.Log
import android.view.TextureView
import android.widget.FrameLayout
import com.faceunity.FURenderer
import io.agora.capture.video.camera.CameraVideoManager
import io.agora.capture.video.camera.Constant
import io.agora.rtc.RtcChannel
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.lang.ref.WeakReference

class RtcTextureView(
  context: Context
) : FrameLayout(context) {
  private var texture: TextureView
  private var canvas: VideoCanvas
  private var channel: WeakReference<RtcChannel>? = null

  init {
    try {
      texture = RtcEngine.CreateTextureView(context)
    } catch (e: UnsatisfiedLinkError) {
      throw RuntimeException("Please init RtcEngine first!")
    }
    canvas = VideoCanvas(texture)
    addView(texture)
  }

  fun setData(engine: RtcEngine, videoManager: CameraVideoManager?, fuRenderer: FURenderer?, channel: RtcChannel?, uid: Number) {
    this.channel = if (channel != null) WeakReference(channel) else null
    canvas.channelId = this.channel?.get()?.channelId()
    canvas.uid = uid.toNativeUInt()
    setupVideoCanvas(engine, videoManager, fuRenderer)
  }

  fun resetVideoCanvas(engine: RtcEngine) {
    val canvas =
      VideoCanvas(null, canvas.renderMode, canvas.channelId, canvas.uid, canvas.mirrorMode)
    if (canvas.uid == 0) {
      engine.setupLocalVideo(canvas)
    } else {
      engine.setupRemoteVideo(canvas)
    }
  }

  private fun setupVideoCanvas(engine: RtcEngine, videoManager: CameraVideoManager?, fuRenderer: FURenderer?) {
    removeAllViews()
    texture = RtcEngine.CreateTextureView(context.applicationContext)
    addView(texture)
    texture.layout(0, 0, width, height)
    if (canvas.uid == 0) {
      if (videoManager != null) {
        videoManager.setLocalPreviewMirror(Constant.MIRROR_MODE_ENABLED)
        videoManager?.setLocalPreview(texture)
        fuRenderer?.onSurfaceCreated()
        videoManager?.startCapture()
        Log.d("RtcEngine", "start capture engine: ${engine} videoManager: ${videoManager} fuRenderer: ${fuRenderer}")
      } else {
        canvas.view = texture
        engine.setupLocalVideo(canvas)
      }
    } else {
      canvas.view = texture
      engine.setupRemoteVideo(canvas)
    }
  }

  fun setRenderMode(engine: RtcEngine, @Annotations.AgoraVideoRenderMode renderMode: Int) {
    canvas.renderMode = renderMode
    setupRenderMode(engine)
  }

  fun setMirrorMode(engine: RtcEngine, @Annotations.AgoraVideoMirrorMode mirrorMode: Int) {
    canvas.mirrorMode = mirrorMode
    setupRenderMode(engine)
  }

  private fun setupRenderMode(engine: RtcEngine) {
    if (canvas.uid == 0) {
      engine.setLocalRenderMode(canvas.renderMode, canvas.mirrorMode)
    } else {
      channel?.get()?.let {
        it.setRemoteRenderMode(canvas.uid, canvas.renderMode, canvas.mirrorMode)
        return@setupRenderMode
      }
      engine.setRemoteRenderMode(canvas.uid, canvas.renderMode, canvas.mirrorMode)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width: Int = MeasureSpec.getSize(widthMeasureSpec)
    val height: Int = MeasureSpec.getSize(heightMeasureSpec)
    texture.layout(0, 0, width, height)
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }
}
