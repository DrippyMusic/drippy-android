ROOT := $(call my-dir)
LOCAL_PATH := $(ROOT)/opus

include $(CLEAR_VARS)
include $(LOCAL_PATH)/celt_sources.mk
include $(LOCAL_PATH)/silk_sources.mk
include $(LOCAL_PATH)/opus_sources.mk

LOCAL_MODULE := opus

LOCAL_SRC_FILES := \
	$(CELT_SOURCES) $(CELT_SOURCES_ARM) \
	$(SILK_SOURCES) $(SILK_SOURCES_FIXED) \
	$(OPUS_SOURCES) $(OPUS_SOURCES_FLOAT)

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/silk \
	$(LOCAL_PATH)/silk/fixed \
	$(LOCAL_PATH)/celt

LOCAL_CFLAGS := -O3 \
	-DNULL=0 \
	-DSOCKLEN_T=socklen_t \
	-DLOCALE_NOT_USED \
	-D_LARGEFILE_SOURCE=1 \
	-D_FILE_OFFSET_BITS=64 \
	-Drestrict='' \
	-D__EMX__ \
	-DOPUS_BUILD \
	-DFIXED_POINT=1 \
	-DUSE_ALLOCA \
	-DHAVE_LRINT \
	-DHAVE_LRINTF \
	-fno-math-errno \
	-DNDEBUG

LOCAL_CPPFLAGS := -O3 \
	-DBSD=1 \
	-ffast-math \
	-funroll-loops

include $(BUILD_STATIC_LIBRARY)