REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_Plan.json: builds and logs the routing plan only (the
rem GenerateFileIndex phase with planOnly forced true by the Plan command) - no model is loaded
rem and nothing is written. Safe to run with no GGUF model on disk (generationProvider is "mock").
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

java -jar "%JARFILE%" config_Plan.json
