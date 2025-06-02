cmake_minimum_required(VERSION 3.31)
project(netrunning C)
# loads GTK configuration from non-standard headers
find_package(PkgConfig REQUIRED)
pkg_check_modules(GTK4 REQUIRED gtk4)

set(CMAKE_C_STANDARD 23)

add_executable(netrunning src/main.c)

# Inclui os diretórios do GTK4
target_include_directories(${PROJECT_NAME} PUBLIC ${GTK4_INCLUDE_DIRS})

# Linka as bibliotecas do GTK4
target_link_directories(${PROJECT_NAME} PRIVATE ${GTK4_LIBRARY_DIRS})
target_link_libraries(${PROJECT_NAME} ${GTK4_LIBRARIES})

# Adiciona as flags de compilação necessárias
target_compile_options(${PROJECT_NAME} PRIVATE ${GTK4_CFLAGS_OTHER})
