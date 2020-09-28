MIN_SDK_VERSION=21
TOOLCHAIN_PATH=$(ANDROID_NDK)/toolchains/llvm/prebuilt/$(HOST)

define DEFAULT_FLAGS
--target-os=android \
--disable-doc \
--disable-everything \
--disable-network \
--disable-autodetect \
--disable-avdevice \
--disable-swscale \
--disable-postproc \
--disable-symver \
--enable-libopus \
--enable-filter=aresample \
--enable-protocol=file
endef

define ENABLED_CODECS
--enable-encoder=libopus \
--enable-decoder=aac,mp3 \
--enable-muxer=opus \
--enable-demuxer=aac,mp3
endef

define EXTRA_ARM_FLAGS
--extra-cflags="-march=armv7-a -mfloat-abi=softfp" \
--extra-ldflags="-Wl,--fix-cortex-a8"
endef

ffmpeg/android-libs/armeabi-v7a: ANDROID_ABI=armeabi-v7a
ffmpeg/android-libs/armeabi-v7a: ARCH=arm
ffmpeg/android-libs/armeabi-v7a: CPU=armv7-a
ffmpeg/android-libs/armeabi-v7a: PREFIX=arm-linux-androideabi
ffmpeg/android-libs/armeabi-v7a: CC_PREFIX=armv7a-linux-androideabi
ffmpeg/android-libs/armeabi-v7a: EXTRA_FLAGS=$(EXTRA_ARM_FLAGS)

ffmpeg/android-libs/arm64-v8a: ANDROID_ABI=arm64-v8a
ffmpeg/android-libs/arm64-v8a: ARCH=aarch64
ffmpeg/android-libs/arm64-v8a: CPU=armv8-a
ffmpeg/android-libs/arm64-v8a: PREFIX=aarch64-linux-android

ffmpeg/android-libs/x86: ANDROID_ABI=x86
ffmpeg/android-libs/x86: ARCH=x86
ffmpeg/android-libs/x86: CPU=i686
ffmpeg/android-libs/x86: PREFIX=i686-linux-android
ffmpeg/android-libs/x86: EXTRA_FLAGS=--disable-asm

ffmpeg/android-libs/x86_64: ANDROID_ABI=x86_64
ffmpeg/android-libs/x86_64: ARCH=x86_64
ffmpeg/android-libs/x86_64: CPU=x86_64
ffmpeg/android-libs/x86_64: PREFIX=x86_64-linux-android
ffmpeg/android-libs/x86_64: EXTRA_FLAGS=--disable-asm

ffmpeg/android-libs/armeabi-v7a ffmpeg/android-libs/arm64-v8a ffmpeg/android-libs/x86 ffmpeg/android-libs/x86_64:
	@./ffmpeg.sh

ffmpeg/android-libs/armeabi-v7a/lib ffmpeg/android-libs/arm64-v8a/lib ffmpeg/android-libs/x86/lib ffmpeg/android-libs/x86_64/lib:
	$(MAKE) -C ffmpeg -j4
	$(MAKE) -C ffmpeg install

ffmpeg/ffbuild/config.mak: opus/autogen.sh
	git submodule update --init ffmpeg
	cd ffmpeg && ./configure --disable-x86asm

opus/autogen.sh:
	git submodule update --init opus
	cd opus && ./autogen.sh

configure: ffmpeg/ffbuild/config.mak

clean:
	@$(MAKE) -s -C ffmpeg clean

dist-clean:
	@rm -rf ffmpeg/android-libs

.SECONDEXPANSION:
armeabi-v7a arm64-v8a x86 x86_64: ffmpeg/android-libs/$$@ ffmpeg/android-libs/$$@/lib

.EXPORT_ALL_VARIABLES:
.PHONY: armeabi-v7a arm64-v8a x86 x86_64 clean dist-clean