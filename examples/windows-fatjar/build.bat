REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Builds target\srcmorph.jar from pom.xml (the CLI plus the natives the pom names). Adapt JAVA_HOME.
cd /d "%~dp0"
set "JAVA_HOME=C:\Program Files\Java\openjdk1.8.0_491"
set "PATH=%JAVA_HOME%\bin;%PATH%"

mvn -B package
