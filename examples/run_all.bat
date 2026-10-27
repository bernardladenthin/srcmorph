REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_All.json: all three phases in order (GenerateFileIndex,
rem AggregatePackages, AggregateProject). Uses the mock provider, so no GGUF model is required -
rem point generationProvider at "llamacpp-jni" and set a real modelPath to run a model.
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

java -jar "%JARFILE%" config_All.json
