REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_Plan.json: builds and logs the routing plan only (the
rem GenerateFileIndex phase with planOnly forced true by the Plan command) - no model is loaded
rem and nothing is written. Safe to run with no GGUF model on disk (generationProvider is "mock").
rem
rem The fat jar's file name is version-qualified (e.g.
rem srcmorph-cli-1.1.0-SNAPSHOT-jar-with-dependencies.jar) and changes on every version bump, so
rem the loop below picks whichever one was last built by "mvn package" in ..\srcmorph-cli.
setlocal
cd /d "%~dp0"

set "JARFILE="
for %%f in (..\srcmorph-cli\target\srcmorph-cli-*-jar-with-dependencies.jar) do set "JARFILE=%%f"
if "%JARFILE%"=="" (
    echo srcmorph-cli fat jar not found under ..\srcmorph-cli\target\ - run "mvn package" first. 1>&2
    exit /b 1
)

java -jar "%JARFILE%" config_Plan.json
