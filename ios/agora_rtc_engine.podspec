#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint agora_rtc_engine.podspec' to validate before publishing.
#
require "yaml"
require "ostruct"
project = OpenStruct.new YAML.load_file("../pubspec.yaml")

Pod::Spec.new do |s|
  s.name             = project.name
  s.version          = project.version
  s.summary          = 'A new flutter plugin project.'
  s.description      = project.description
  s.homepage         = 'https://github.com/AgoraIO/Flutter-SDK'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Agora' => 'developer@agora.io' }
  s.source           = { :git => 'https://github.com/ldcgeek/faceunity-ios.git', :tag => '1.0.0' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'AgoraRtcEngine_iOS', '3.5.0'
  s.dependency 'FaceUnity'
  s.platform = :ios, '9.0'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64', 'OTHER_LDFLAGS' => '$(inherited) -ObjC -lc++ -framework AGMBase -framework AGMCapturer -framework AGMRenderer -all_load' }
  s.swift_version = '4.0'
end
