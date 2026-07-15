REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: Apache-2.0

@echo off
rem Runs the srcmorph CLI against config_All.json: all three phases in order (GenerateFileIndex,
rem AggregatePackages, AggregateProject). Uses the mock provider, so no GGUF model is required -
rem point generationProvider at "llamacpp-jni" and set a real modelPath to run a model.
rem
rem The fat jar's file name is version-qualified (e.g.
rem srcmorph-cli-1.1.0-jar-with-dependencies.jar) and changes on every version bump, so
rem the loop below picks whichever one was last built by "mvn package" in ..\srcmorph-cli.
setlocal
cd /d "%~dp0"

set "JARFILE="
for %%f in (..\srcmorph-cli\target\srcmorph-cli-*-jar-with-dependencies.jar) do set "JARFILE=%%f"
if "%JARFILE%"=="" (
    echo srcmorph-cli fat jar not found under ..\srcmorph-cli\target\ - run "mvn package" first. 1>&2
    exit /b 1
)

java -jar "%JARFILE%" config_All.json
