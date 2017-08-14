LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := CryptHelper
LOCAL_SRC_FILES := CryptHelper.c

include $(BUILD_SHARED_LIBRARY)