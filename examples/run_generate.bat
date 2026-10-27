REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_GenerateFileIndex.json (Phase 1 only: index source files
rem and fill in their AI-generated summary bodies). Uses the mock provider, so no GGUF model is
rem required - point generationProvider at "llamacpp-jni" and set a real modelPath to run a model.
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

java -jar "%JARFILE%" config_GenerateFileIndex.json
