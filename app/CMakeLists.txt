cmake_minimum_required(VERSION 3.4.1)

get_filename_component(PROJECT_DIR ../ ABSOLUTE)
set(FFMPEG_BUILD ${PROJECT_DIR}/ffmpeg/build/${ANDROID_ABI})

add_library(native-lib SHARED src/main/cpp/native-lib.cpp)
include_directories(native-lib ${FFMPEG_DIR}/include)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_BUILD}/lib/libavutil.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_BUILD}/lib/libswresample.so)

add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_BUILD}/lib/libavcodec.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_BUILD}/lib/libavformat.so)

target_link_libraries(native-lib avutil swresample avcodec avformat)