# CMakeLists.txt

# Copyright (C) 2007,2009-2016 Glenn Randers-Pehrson
# Written by Christian Ehrlicher, 2007
# Revised by Roger Lowman, 2009-2010
# Revised by Clifford Yapp, 2011-2012
# Revised by Roger Leigh, 2016
# Revised by Andreas Franek, 2016

# This code is released under the libpng license.
# For conditions of distribution and use, see the disclaimer
# and license in png.h

cmake_minimum_required(VERSION 2.8.3)
cmake_policy(VERSION 2.8.3)

# set definitions and sources for arm
if(${CMAKE_SYSTEM_PROCESSOR} MATCHES "^arm" OR
  ${CMAKE_SYSTEM_PROCESSOR} MATCHES "^aarch64")
  set(PNG_ARM_NEON_POSSIBLE_VALUES check on off)
  set(PNG_ARM_NEON "check" CACHE STRING "Enable ARM NEON optimizations:
     check: (default) use internal checking code;
     off: disable the optimizations;
     on: turn on unconditionally.")
  set_property(CACHE PNG_ARM_NEON PROPERTY STRINGS
     ${PNG_ARM_NEON_POSSIBLE_VALUES})
  list(FIND PNG_ARM_NEON_POSSIBLE_VALUES ${PNG_ARM_NEON} index)
  if(index EQUAL -1)
    message(FATAL_ERROR
      " PNG_ARM_NEON must be one of [${PNG_ARM_NEON_POSSIBLE_VALUES}]")
  elseif(NOT ${PNG_ARM_NEON} STREQUAL "no")
    set(libpng_arm_sources
      arm/arm_init.c
      arm/filter_neon.S
      arm/filter_neon_intrinsics.c
      )
    if(${PNG_ARM_NEON} STREQUAL "on")
      add_definitions(-DPNG_ARM_NEON_OPT=2)
    elseif(${PNG_ARM_NEON} STREQUAL "check")
      add_definitions(-DPNG_ARM_NEON_CHECK_SUPPORTED)
      #set(SOURCE ${libpng_arm_sources} APPEND_STRING PROPERTY COMPILE_FLAGS " -mfcpu=neon")
    endif()
  else()
    add_definitions(-DPNG_ARM_NEON_OPT=0)
  endif()
endif()



configure_file(${CMAKE_CURRENT_SOURCE_DIR}/scripts/pnglibconf.h.prebuilt
                 ${CMAKE_CURRENT_BINARY_DIR}/pnglibconf.h)



# OUR SOURCES
set(libpng_public_hdrs
  png.h
  pngconf.h
  "${CMAKE_CURRENT_BINARY_DIR}/pnglibconf.h"
)
set(libpng_private_hdrs
  pngpriv.h
  pngdebug.h
  pnginfo.h
  pngstruct.h
)

set(libpng_sources
  ${libpng_public_hdrs}
  ${libpng_private_hdrs}
  png.c
pngerror.c
  pngget.c
  pngmem.c
  pngpread.c
  pngread.c
  pngrio.c
  pngrtran.c
  pngrutil.c
  pngset.c
  pngtrans.c
  pngwio.c
  pngwrite.c
  pngwtran.c
  pngwutil.c
  ${libpng_arm_sources}
)
#add_library(cpufeatures STATIC ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c)
add_library(png SHARED ${libpng_sources})
#target_include_directories(png PRIVATE ${ANDROID_NDK}/sources/android/cpufeatures)
TARGET_LINK_LIBRARIES(png z)

