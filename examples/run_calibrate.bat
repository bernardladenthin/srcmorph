REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_Calibrate.json: loads each distinct routed model once
rem and prints a paste-ready <calibration> block per model. With the mock provider (as shipped)
rem this is a no-op smoke check; swap generationProvider to "llamacpp-jni" and set a real
rem modelPath to calibrate an actual GGUF model on this machine.
rem
rem The fat jar's file name is version-qualified (e.g.
rem srcmorph-cli-1.2.0-SNAPSHOT-jar-with-dependencies.jar) and changes on every version bump, so
rem the loop below picks whichever one was last built by "mvn package" in ..\srcmorph-cli.
setlocal
cd /d "%~dp0"

set "JARFILE="
for %%f in (..\srcmorph-cli\target\srcmorph-cli-*-jar-with-dependencies.jar) do set "JARFILE=%%f"
if "%JARFILE%"=="" (
    echo srcmorph-cli fat jar not found under ..\srcmorph-cli\target\ - run "mvn package" first. 1>&2
    exit /b 1
)

java -jar "%JARFILE%" config_Calibrate.json
