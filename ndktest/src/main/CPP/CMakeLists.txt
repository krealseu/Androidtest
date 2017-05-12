# CMakeLists.txt
cmake_minimum_required(VERSION 2.8.3)
ADD_SUBDIRECTORY(libpng)
INCLUDE_DIRECTORIES(libpng)

ADD_LIBRARY(qweqwe SHARED test.cpp)
TARGET_LINK_LIBRARIES(qweqwe png android log)

#ADD_SUBDIRECTORY(libjpeg)
