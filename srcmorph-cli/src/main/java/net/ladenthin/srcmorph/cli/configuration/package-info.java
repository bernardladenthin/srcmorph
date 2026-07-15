// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * The CLI's own configuration wrapper: {@link net.ladenthin.srcmorph.cli.configuration.CConfiguration}
 * (the JSON/YAML document root) and {@link net.ladenthin.srcmorph.cli.configuration.CCommand} (the
 * selected phase), bindable via Jackson's {@code ObjectMapper} / {@code YAMLMapper}
 * (BitcoinAddressFinder {@code configuration} package convention: public mutable-field JavaBeans).
 */
package net.ladenthin.srcmorph.cli.configuration;
