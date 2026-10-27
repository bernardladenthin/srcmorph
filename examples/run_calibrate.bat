REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_Calibrate.json: loads each distinct routed model once
rem and prints a paste-ready <calibration> block per model. With the mock provider (as shipped)
rem this is a no-op smoke check; swap generationProvider to "llamacpp-jni" and set a real
rem modelPath to calibrate an actual GGUF model on this machine.
rem
rem Runs the jar of build-your-own-jar\ (srcmorph-cli plus the natives of this machine); build it
rem once with build-your-own-jar\build.bat.
setlocal
cd /d "%~dp0"

set "JARFILE=build-your-own-jar\target\srcmorph.jar"
if not exist "%JARFILE%" (
    echo %JARFILE% not found - run build-your-own-jar\build.bat first. 1>&2
    exit /b 1
)

java -jar "%JARFILE%" config_Calibrate.json
