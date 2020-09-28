MIN_SDK_VERSION=21
TOOLCHAIN_PATH=$(ANDROID_NDK)/toolchains/llvm/prebuilt/$(HOST)/bin
ANDROID_LIBS=$(CURDIR)/ffmpeg/android-libs

define EXTRA_FLAGS
--extra-cflags="-I$(ANDROID_LIBS)/external/include" \
--extra-ldflags="-L$(ANDROID_LIBS)/external/lib"
endef

define DEFAULT_FLAGS
--target-os=android \
--disable-static \
--enable-shared \
--disable-doc \
--disable-programs \
--disable-everything \
--disable-avdevice \
--disable-avformat \
--disable-swscale \
--disable-postproc \
--disable-avfilter \
--disable-symver \
--disable-avresample \
--enable-swresample \
--enable-libopus
endef

define ENABLED_CODECS
--enable-encoder=opus \
--enable-decoder=mp3 \
--enable-decoder=aac \
--enable-parser=mpegaudio \
--enable-parser=aac
endef

ffmpeg/android-libs/armeabi-v7a:
	cd ffmpeg && \
	./configure --prefix=android-libs/armeabi-v7a --arch=arm --cpu=armv7-a \
		--cross-prefix="$(TOOLCHAIN_PATH)/arm-linux-androideabi-" \
		--cc="$(TOOLCHAIN_PATH)/armv7a-linux-androideabi$(MIN_SDK_VERSION)-clang" \
		--extra-cflags="-march=armv7-a -mfloat-abi=softfp" \
		--extra-ldflags="-Wl,--fix-cortex-a8" \
		$(EXTRA_FLAGS) \
		$(DEFAULT_FLAGS) $(ENABLED_CODECS)

ffmpeg/android-libs/arm64-v8a:
	cd ffmpeg && \
	./configure --prefix=android-libs/arm64-v8a --arch=aarch64 --cpu=armv8-a \
		--cross-prefix="$(TOOLCHAIN_PATH)/aarch64-linux-android-" \
		--cc="$(TOOLCHAIN_PATH)/aarch64-linux-android$(MIN_SDK_VERSION)-clang" \
		$(EXTRA_FLAGS) \
		$(DEFAULT_FLAGS) $(ENABLED_CODECS)

ffmpeg/android-libs/x86:
	cd ffmpeg && \
	./configure --prefix=android-libs/x86 --arch=x86 --cpu=i686 \
		--cross-prefix="$(TOOLCHAIN_PATH)/i686-linux-android-" \
		--cc="$(TOOLCHAIN_PATH)/i686-linux-android$(MIN_SDK_VERSION)-clang" \
		$(EXTRA_FLAGS) \
		--disable-asm $(DEFAULT_FLAGS) $(ENABLED_CODECS)

ffmpeg/android-libs/x86_64:
	cd ffmpeg && \
	./configure --prefix=android-libs/x86_64 --arch=x86_64 --cpu=x86_64 \
		--cross-prefix="$(TOOLCHAIN_PATH)/x86_64-linux-android-" \
		--cc="$(TOOLCHAIN_PATH)/x86_64-linux-android$(MIN_SDK_VERSION)-clang" \
		$(EXTRA_FLAGS) \
		--disable-asm $(DEFAULT_FLAGS) $(ENABLED_CODECS)

ffmpeg/android-libs/armeabi-v7a/lib ffmpeg/android-libs/arm64-v8a/lib ffmpeg/android-libs/x86/lib ffmpeg/android-libs/x86_64/lib: ffmpeg/android-libs/external/lib/libopus.a
	$(MAKE) -C ffmpeg -j4
	$(MAKE) -C ffmpeg install

ffmpeg/android-libs/external/lib/libopus.a:
	$(MAKE) -C opus -j4
	$(MAKE) -C opus install

ffmpeg/ffbuild/config.mak: opus/Makefile
	git submodule update --init ffmpeg
	cd ffmpeg && ./configure --disable-x86asm

opus/Makefile: opus/autogen.sh
	cd opus && \
	./configure --prefix=$(ANDROID_LIBS)/external \
		--disable-shared --disable-extra-programs --disable-doc

opus/autogen.sh:
	git submodule update --init opus
	cd opus && ./autogen.sh

configure: ffmpeg/ffbuild/config.mak

clean:
	@$(MAKE) -s -C ffmpeg clean

dist-clean:
	@$(MAKE) -s -C opus clean
	@rm -rf ffmpeg/android-libs

.SECONDEXPANSION:
armeabi-v7a arm64-v8a x86 x86_64: ffmpeg/android-libs/$$@ ffmpeg/android-libs/$$@/lib

.PHONY: armeabi-v7a arm64-v8a x86 x86_64 clean dist-clean