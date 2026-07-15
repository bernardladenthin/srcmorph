// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * Per-phase orchestration engines, extracted from the {@code llamacpp-ai-index-maven-plugin} module's
 * mojo {@code execute()} bodies so a run can be driven from plain Java (and, eventually, a CLI) without a
 * Maven runtime. Sits above every other package in this library.
 */
package net.ladenthin.srcmorph.engine;
