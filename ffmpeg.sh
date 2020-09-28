#!/bin/sh
PKG_CONFIG=$PWD/pkg-config
export OPUS_DIR=$PWD/opus

[ -z "${CC_PREFIX}" ] && CC_PREFIX=${PREFIX}

echo "Configuration: '$ANDROID_ABI' $ARCH $CPU"

cd ffmpeg &&
./configure --prefix=android-libs/${ANDROID_ABI} --arch=${ARCH} --cpu=${CPU} \
    --cross-prefix="${TOOLCHAIN_PATH}/bin/${PREFIX}-" \
	--cc="${TOOLCHAIN_PATH}/bin/${CC_PREFIX}${MIN_SDK_VERSION}-clang" \
    --sysroot="${TOOLCHAIN_PATH}/sysroot" --pkg-config="${PKG_CONFIG}" \
    --extra-cflags="-O3 -fPIC" ${EXTRA_FLAGS} ${DEFAULT_FLAGS} ${ENABLED_CODECS}