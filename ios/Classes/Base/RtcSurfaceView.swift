//
//  RtcSurfaceView.swift
//  RCTAgora
//
//  Created by LXH on 2020/4/15.
//  Copyright © 2020 Syan. All rights reserved.
//

import AgoraRtcKit
import Foundation
import UIKit

class RtcSurfaceView: UIView {
    private var surface: UIView
    private var canvas: AgoraRtcVideoCanvas
    private weak var channel: AgoraRtcChannel?
    private var glVideoView: AGMEAGLVideoView?

    override init(frame: CGRect) {
        surface = UIView(frame: CGRect(origin: CGPoint(x: 0, y: 0), size: frame.size))
        canvas = AgoraRtcVideoCanvas()
        canvas.view = surface
        super.init(frame: frame)
        addSubview(surface)
        addObserver(self, forKeyPath: observerForKeyPath(), options: .new, context: nil)
    }

    func observerForKeyPath() -> String {
        return "frame"
    }

    @available(*, unavailable)
    required init?(coder _: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    deinit {
        canvas.view = nil
        removeObserver(self, forKeyPath: observerForKeyPath(), context: nil)
    }

    func setData(_ engine: AgoraRtcEngineKit, _ capturerManager: CapturerManager?, _ channel: AgoraRtcChannel?, _ uid: UInt) {
        self.channel = channel
        canvas.channelId = channel?.getId()
        canvas.uid = uid
        setupVideoCanvas(engine, capturerManager)
    }

    func resetVideoCanvas(_ engine: AgoraRtcEngineKit) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = nil
        canvas.renderMode = self.canvas.renderMode
        canvas.channelId = self.canvas.channelId
        canvas.uid = self.canvas.uid
        canvas.mirrorMode = self.canvas.mirrorMode

        if canvas.uid == 0 {
            engine.setupLocalVideo(canvas)
        } else {
            engine.setupRemoteVideo(canvas)
        }
    }

    private func setupVideoCanvas(_ engine: AgoraRtcEngineKit, _ capturerManager: CapturerManager?) {
        subviews.forEach {
            $0.removeFromSuperview()
        }
        surface = UIView(frame: CGRect(origin: CGPoint(x: 0, y: 0), size: bounds.size))
        addSubview(surface)
        canvas.view = surface
        if canvas.uid == 0 {
            capturerManager?.startCapture()
            engine.setVideoSource(capturerManager!)
            engine.setupLocalVideo(canvas)
            engine.startPreview()
            engine.setLocalRenderMode(AgoraVideoRenderMode.hidden, mirrorMode: AgoraVideoMirrorMode.disabled)
        } else {
            print("setup remote video uid: \(canvas.uid)")
            engine.setupRemoteVideo(canvas)
        }
    }

    func setRenderMode(_ engine: AgoraRtcEngineKit, _ renderMode: UInt) {
//        canvas.renderMode = AgoraVideoRenderMode(rawValue: renderMode)!
//        setupRenderMode(engine)
    }

    func setMirrorMode(_ engine: AgoraRtcEngineKit, _ mirrorMode: UInt) {
//        canvas.mirrorMode = AgoraVideoMirrorMode(rawValue: mirrorMode)!
//        setupRenderMode(engine)
    }

    private func setupRenderMode(_ engine: AgoraRtcEngineKit) {
        if canvas.uid == 0 {
            engine.setLocalRenderMode(canvas.renderMode, mirrorMode: canvas.mirrorMode)
        } else {
            if let channel = channel {
                channel.setRemoteRenderMode(canvas.uid, renderMode: canvas.renderMode, mirrorMode: canvas.mirrorMode)
            } else {
                engine.setRemoteRenderMode(canvas.uid, renderMode: canvas.renderMode, mirrorMode: canvas.mirrorMode)
            }
        }
    }

    override func observeValue(forKeyPath keyPath: String?, of _: Any?, change: [NSKeyValueChangeKey: Any]?, context _: UnsafeMutableRawPointer?) {
        if keyPath == observerForKeyPath() {
            if let rect = change?[.newKey] as? CGRect {
                surface.frame = CGRect(origin: CGPoint(x: 0, y: 0), size: rect.size)
            }
        }
    }
}
