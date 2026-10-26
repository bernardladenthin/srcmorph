REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Builds target\srcmorph.jar from pom.xml (the CLI plus the natives of this OS/arch).
rem Extra arguments go to Maven, e.g. build.bat -Dgpu=vulkan-windows-x86-64
cd /d "%~dp0"
mvn -B package %*
