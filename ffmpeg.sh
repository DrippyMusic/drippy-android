#!/bin/sh
PKG_CONFIG=$PWD/pkg-config
export OPUS_DIR=$PWD/opus

echo "Configuration: '$ANDROID_ABI' $ARCH $CPU"

EXTRA_CFLAGS="-O3 -fPIC"
[ -z "${CC_PREFIX}" ] && CC_PREFIX=${PREFIX}

if [ $ANDROID_ABI = "x86" ] || [ $ANDROID_ABI = "x86_64" ]; then
    DEFAULT_FLAGS="--disable-asm ${DEFAULT_FLAGS}"
fi

if [ $ANDROID_ABI = "armeabi-v7a" ]; then
    EXTRA_CFLAGS="${EXTRA_CFLAGS} -march=armv7-a -mfloat-abi=softfp"
    EXTRA_LDFLAGS="-Wl,--fix-cortex-a8"
fi

cd ffmpeg &&
    ./configure --prefix=android-libs/${ANDROID_ABI} --arch=${ARCH} --cpu=${CPU} \
        --cross-prefix="${TOOLCHAIN_PATH}/bin/${PREFIX}-" \
        --cc="${TOOLCHAIN_PATH}/bin/${CC_PREFIX}${MIN_SDK_VERSION}-clang" \
        --sysroot="${TOOLCHAIN_PATH}/sysroot" --pkg-config="${PKG_CONFIG}" \
        --extra-cflags="${EXTRA_CFLAGS}" --extra-ldflags="${EXTRA_LDFLAGS}" \
        ${DEFAULT_FLAGS} ${ENABLED_CODECS}
