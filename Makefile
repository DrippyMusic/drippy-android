MIN_SDK_VERSION=21
TOOLCHAIN_PATH=$(ANDROID_NDK)/toolchains/llvm/prebuilt/$(HOST)

ANDROID_LIBS=ffmpeg/android-libs

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
--enable-protocol=file,pipe
endef

define ENABLED_CODECS
--enable-encoder=libopus \
--enable-decoder=aac,mp3 \
--enable-muxer=opus \
--enable-demuxer=aac,mp3
endef

$(ANDROID_LIBS)/armeabi-v7a: ANDROID_ABI=armeabi-v7a
$(ANDROID_LIBS)/armeabi-v7a: ARCH=arm
$(ANDROID_LIBS)/armeabi-v7a: CPU=armv7-a
$(ANDROID_LIBS)/armeabi-v7a: PREFIX=arm-linux-androideabi
$(ANDROID_LIBS)/armeabi-v7a: CC_PREFIX=armv7a-linux-androideabi

$(ANDROID_LIBS)/arm64-v8a: ANDROID_ABI=arm64-v8a
$(ANDROID_LIBS)/arm64-v8a: ARCH=aarch64
$(ANDROID_LIBS)/arm64-v8a: CPU=armv8-a
$(ANDROID_LIBS)/arm64-v8a: PREFIX=aarch64-linux-android

$(ANDROID_LIBS)/x86: ANDROID_ABI=x86
$(ANDROID_LIBS)/x86: ARCH=x86
$(ANDROID_LIBS)/x86: CPU=i686
$(ANDROID_LIBS)/x86: PREFIX=i686-linux-android

$(ANDROID_LIBS)/x86_64: ANDROID_ABI=x86_64
$(ANDROID_LIBS)/x86_64: ARCH=x86_64
$(ANDROID_LIBS)/x86_64: CPU=x86_64
$(ANDROID_LIBS)/x86_64: PREFIX=x86_64-linux-android

$(ANDROID_LIBS)/armeabi-v7a $(ANDROID_LIBS)/arm64-v8a $(ANDROID_LIBS)/x86 $(ANDROID_LIBS)/x86_64:
	@./ffmpeg.sh && \
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
	@rm -rf $(ANDROID_LIBS)

.SECONDEXPANSION:
armeabi-v7a arm64-v8a x86 x86_64: $(ANDROID_LIBS)/$$@

.EXPORT_ALL_VARIABLES:
.PHONY: armeabi-v7a arm64-v8a x86 x86_64 clean dist-clean