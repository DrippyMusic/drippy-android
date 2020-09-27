@echo off
setlocal ENABLEDELAYEDEXPANSION
set HOST=windows-x86_64

where make > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Please make sure you have MinGW and MSYS installed
    exit /b
)

if %1.==. (
    for /d %%a in (%LOCALAPPDATA%\Android\Sdk\ndk\*) do (
        set ANDROID_NDK=%%~a
    )
) else (
    set ANDROID_NDK=%1
)

for /f %%i in ('echo %ANDROID_NDK% ^| sed -E "s/^.*$/\l&/ ; s/\\\/\//g ; s/://g"') do (
    set ANDROID_NDK=/%%i
)

make configure
make clean && make armeabi-v7a
make clean && make arm64-v8a
make clean && make x86
make clean && make x86_64