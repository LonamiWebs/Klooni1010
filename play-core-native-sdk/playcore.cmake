# Copyright 2019 Google LLC
#
# The Play Core Native SDK is licensed to you under the Android Software
# Development Kit License Agreement -
# https://developer.android.com/studio/terms ("Agreement"). By using the Play
# Core Native SDK, you agree to the terms of this Agreement.

# Provides helper functions to build with the Play Core Native SDK using CMake.

cmake_minimum_required(VERSION 3.6)

set(PLAYCORE_LIBS_ABI_DIR ${CMAKE_CURRENT_LIST_DIR}/libs/${ANDROID_ABI})

function(add_playcore_static_library)
    if (NOT ANDROID_NDK_MAJOR)
        # Workaround for NDK r16 and earlier which don't set ANDROID_NDK_MAJOR.
        if (NOT ANDROID_NDK_SOURCE_PROPERTIES MATCHES ${ANDROID_NDK_SOURCE_PROPERTIES_REGEX})
            message(FATAL_ERROR "Failed to parse Android NDK revision: ${ANDROID_NDK_SOURCE_PROPERTIES}")
        endif ()
        set(ANDROID_NDK_MAJOR "${CMAKE_MATCH_1}")
    endif ()

    # Find all candidate static library paths.
    file(GLOB PLAYCORE_NDK_DIRS "${PLAYCORE_LIBS_ABI_DIR}/ndk*")
    if (NOT PLAYCORE_NDK_DIRS)
        message(FATAL_ERROR "Failed to find PlayCore libs in directory: ${PLAYCORE_LIBS_ABI_DIR}")
    endif ()

    # Extract the NDK major version from each candidate static library path.
    set(PLAYCORE_NDK_MAJOR_VERSION_REGEX "/ndk([0-9]+)\\.[^/]+")
    foreach (PLAYCORE_NDK_DIR ${PLAYCORE_NDK_DIRS})
        if (NOT PLAYCORE_NDK_DIR MATCHES ${PLAYCORE_NDK_MAJOR_VERSION_REGEX})
            message(FATAL_ERROR "Failed to parse NDK version from directory: ${PLAYCORE_NDK_DIR}")
        endif ()
        list(APPEND PLAYCORE_NDK_MAJOR_VERSIONS ${CMAKE_MATCH_1})
    endforeach ()

    # Find the static library with the best NDK major version.
    set(PLAYCORE_BEST_MAJOR 0)
    foreach (PLAYCORE_CURRENT_MAJOR ${PLAYCORE_NDK_MAJOR_VERSIONS})
        # Workaround for CMake 3.6 not having the "LESS_EQUAL" operator.
        if (${PLAYCORE_CURRENT_MAJOR} GREATER ${PLAYCORE_BEST_MAJOR}
                AND (${PLAYCORE_CURRENT_MAJOR} LESS ${ANDROID_NDK_MAJOR}
                OR ${PLAYCORE_CURRENT_MAJOR} EQUAL ${ANDROID_NDK_MAJOR}))
            set(PLAYCORE_BEST_MAJOR ${PLAYCORE_CURRENT_MAJOR})
        endif ()
    endforeach ()

    if (PLAYCORE_BEST_MAJOR EQUAL 0)
        message(FATAL_ERROR "Failed to pick an NDK revision in directory: ${PLAYCORE_LIBS_ABI_DIR}")
    endif ()

    # Find the path corresponding to the best static library.
    foreach (PLAYCORE_NDK_DIR ${PLAYCORE_NDK_DIRS})
        string(REGEX MATCH ${PLAYCORE_NDK_MAJOR_VERSION_REGEX} PLAYCORE_IGNORED ${PLAYCORE_NDK_DIR})
        if (${PLAYCORE_BEST_MAJOR} EQUAL ${CMAKE_MATCH_1})
            set(PLAYCORE_LIBS_NDK_DIR ${PLAYCORE_NDK_DIR})
            break()
        endif ()
    endforeach ()

    if (NOT PLAYCORE_LIBS_NDK_DIR)
        message(FATAL_ERROR "Failed to find path for version ${PLAYCORE_BEST_MAJOR} in directory: ${PLAYCORE_LIBS_ABI_DIR}")
    endif ()

    # Add the static library to the build.
    add_library(playcore STATIC IMPORTED)
    set_target_properties(playcore
            PROPERTIES
            IMPORTED_LOCATION ${PLAYCORE_LIBS_NDK_DIR}/${ANDROID_STL}/libplaycore_static.a)
endfunction()
