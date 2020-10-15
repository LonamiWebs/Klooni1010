#cmake_minimum_required(VERSION 3.6)
#
#
## Add a static library called “playcore” built with the c++_static STL.
#include(../../play-core-native-sdk/playcore.cmake)
#add_playcore_static_library()
#
#add_library(main SHARED)
#
#target_include_directories(main PRIVATE
#        ../../play-core-native-sdk/include)
#
#target_link_libraries(main
#        android
#        playcore)