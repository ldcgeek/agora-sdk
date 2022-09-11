package io.agora.rtc.base

import com.faceunity.OnFUControlListener

// 美颜设置
data class BeautySetting @JvmOverloads constructor(
    // 美肤设置
    val skin: BeautySkin,
    // 美型设置
    val shape: BeautyShape,
    // 滤镜名称
    val filterName: String = "origin",
    // 滤镜级别
    val filterLevel: Float = 0f
)

// 美肤参数，要和相芯科技 BeautyParameterModel 一致
data class BeautySkin(
    // 精细磨皮
    val blurLevel: Float = 0.7f,
    // 美白
    val colorLevel: Float = 0.8f,
    // 红润
    val redLevel: Float = 0.2f,
    // 亮眼
    val eyeBright: Float = 0f,
    // 美牙
    val toothWhiten: Float = 0f,
    // 去黑眼圈
    val microPouch: Float = 0f,
    // 去法令纹
    val microNasolabialFolds: Float = 0f
)


// 美型参数，要和相芯科技 BeautyParameterModel 一致
data class BeautyShape(
    // 瘦脸
    val cheekThinning: Float = 0.2f,
    // V脸
    val cheekV: Float = 0.2f,
    // 窄脸
    val cheekNarrow: Float = 0.2f,
    // 小脸
    val cheekSmall: Float = 0f,
    // 大眼
    val eyeEnlarging: Float = 0f,
    // 下巴
    val intensityChin: Float = 0f,
    // 额头
    val intensityForehead: Float = 0f,
    // 瘦鼻
    val intensityNose: Float = 0f,
    // 嘴型
    val intensityMouth: Float = 0f,
    // 开眼角
    val microCanthus: Float = 0f,
    // 眼距
    val microEyeSpace: Float = 0f,
    // 眼睛角度
    val microEyeRotate: Float = 0f,
    // 长鼻
    val microLongNose: Float = 0f,
    // 缩人中
    val microPhiltrum: Float = 0f,
    // 微笑嘴角
    val microSmile: Float = 0f
)


object BeautyUtils {
    fun toRender(s: BeautySetting, render: OnFUControlListener) {
        // 美肤
        render.onBlurLevelSelected(s.skin.blurLevel)
        render.onColorLevelSelected(s.skin.colorLevel)
        render.onRedLevelSelected(s.skin.redLevel)
        render.onEyeBrightSelected(s.skin.eyeBright)
        render.onToothWhitenSelected(s.skin.toothWhiten)
        render.setRemovePouchStrength(s.skin.microPouch)
        render.setRemoveNasolabialFoldsStrength(s.skin.microNasolabialFolds)

        // 美型
        render.onCheekThinningSelected(s.shape.cheekThinning)
        render.onCheekVSelected(s.shape.cheekV)
        render.onCheekNarrowSelected(s.shape.cheekNarrow)
        render.onCheekSmallSelected(s.shape.cheekSmall)
        render.onEyeEnlargeSelected(s.shape.eyeEnlarging)
        render.onIntensityChinSelected(s.shape.intensityChin)
        render.onIntensityForeheadSelected(s.shape.intensityForehead)
        render.onIntensityNoseSelected(s.shape.intensityNose)
        render.onIntensityMouthSelected(s.shape.intensityMouth)
        render.setCanthusIntensity(s.shape.microCanthus)
        render.setEyeSpaceIntensity(s.shape.microEyeSpace)
        render.setEyeRotateIntensity(s.shape.microEyeRotate)
        render.setLongNoseIntensity(s.shape.microLongNose)
        render.setPhiltrumIntensity(s.shape.microPhiltrum)
        render.setSmileIntensity(s.shape.microSmile)

        // 滤镜
        if (!s.filterName.isNullOrBlank()) {
            render.onFilterNameSelected(s.filterName)
            render.onFilterLevelSelected(s.filterLevel)
        }

        render.setBeautificationOn(true)
    }
}