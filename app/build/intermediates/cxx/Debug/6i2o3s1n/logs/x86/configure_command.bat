@echo off
"C:\\Users\\Wehtt\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Wehtt\\OneDrive\\Pictures\\AuraFrameFX-Alpha-AuraXoS\\AuraFrameFX-Alpha-AuraXoS\\app\\src\\main\\cpp" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=33" ^
  "-DANDROID_PLATFORM=android-33" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Wehtt\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Wehtt\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Wehtt\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Wehtt\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Wehtt\\OneDrive\\Pictures\\AuraFrameFX-Alpha-AuraXoS\\AuraFrameFX-Alpha-AuraXoS\\app\\build\\intermediates\\cxx\\Debug\\6i2o3s1n\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Wehtt\\OneDrive\\Pictures\\AuraFrameFX-Alpha-AuraXoS\\AuraFrameFX-Alpha-AuraXoS\\app\\build\\intermediates\\cxx\\Debug\\6i2o3s1n\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Wehtt\\OneDrive\\Pictures\\AuraFrameFX-Alpha-AuraXoS\\AuraFrameFX-Alpha-AuraXoS\\app\\.cxx\\Debug\\6i2o3s1n\\x86" ^
  -GNinja
