// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

/** Identifies whether an AI generation operates on a single source file or a whole package. */
public enum AiGenerationKind {
    /** Generation step that produces fields for a single source file. */
    FILE_SUMMARY,
    /** Generation step that produces fields for a package aggregate. */
    PACKAGE_SUMMARY
}
