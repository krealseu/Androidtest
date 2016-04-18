LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ccmediadecoder


LOCAL_C_INCLUDES := $(LOCAL_PATH)/../ffmpeg/include

# Add your application source files here...
LOCAL_SRC_FILES := ccMediaDecoder.c

LOCAL_SHARED_LIBRARIES := ffmpeg

LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -llog

include $(BUILD_SHARED_LIBRARY)
