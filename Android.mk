LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := telephony-common

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v7-palette \
    android-support-v4 \
    android-support-v13 \
    android-support-v7-recyclerview \
    parse \
    bolts_tasks \
    bolts_applinks \
    play_78 \
    eventbus \
    gson \
    pubnub

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := AntiTheft
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

# Include res dir from chips
google_play_dir := google-play-services_lib/res
res_dir := $(google_play_dir) res

#LOCAL_RESOURCE_DIR := \
#    $(LOCAL_PATH)/res \
#    $(addprefix $(LOCAL_PATH)/, $(res_dir))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dir))

LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages com.android.keyguard
LOCAL_AAPT_FLAGS += --extra-packages com.google.android.gms

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    parse:libs/parse-android-1.13.0.jar \
    bolts_tasks:libs/bolts-tasks-1.4.0.jar \
    bolts_applinks:libs/bolts-applinks-1.4.0.jar \
    play_78:google-play-services_lib/libs/google-play-services.jar \
    eventbus:libs/eventbus-2.4.1.jar \
    pubnub:libs/Pubnub-Android-3.7.7.jar
#    gson:libs/gson-2.5.jar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
