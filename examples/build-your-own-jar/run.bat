REM SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
REM
REM SPDX-License-Identifier: MIT OR Apache-2.0

@echo off
rem Runs srcmorph with srcmorph.yaml from this directory (or the configuration named as argument).
cd /d "%~dp0"
if "%~1"=="" (java -jar target\srcmorph.jar srcmorph.yaml) else (java -jar target\srcmorph.jar "%~1")
