REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs srcmorph with srcmorph.yaml from this directory. Adapt JAVA_HOME.
cd /d "%~dp0"
set "JAVA_HOME=C:\Program Files\Java\openjdk1.8.0_491"
set "PATH=%JAVA_HOME%\bin;%PATH%"

java -jar target\srcmorph.jar srcmorph.yaml
