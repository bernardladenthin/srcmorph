// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * The version of srcmorph itself, as the build wrote it into {@value #RESOURCE} next to this class: what
 * every summary records as its generator ({@code G}) unless the configuration names another
 * ({@code generatorVersion}). The CLI and the plugin record the same version this way, the one of the
 * library that wrote the summary.
 */
@ToString
public final class GeneratorVersion {

    /** The resource next to this class that holds the version, filled in by the build's resource filtering. */
    static final String RESOURCE = "srcmorph-version.properties";

    /** The key of the version in {@link #RESOURCE}. */
    static final String VERSION_KEY = "version";

    /** What a version that cannot be read is: no generator version recorded. */
    static final String UNKNOWN = "";

    /** Creates a new {@link GeneratorVersion}. */
    public GeneratorVersion() {
        // no-op
    }

    /**
     * Returns srcmorph's version.
     *
     * @return the version, {@code ""} when the resource is missing or unreadable
     */
    public String version() {
        return read(GeneratorVersion.class.getResourceAsStream(RESOURCE));
    }

    /**
     * Reads the version from a properties stream and closes it.
     *
     * @param stream the stream of {@link #RESOURCE}, {@code null} when there is none
     * @return the trimmed version, {@link #UNKNOWN} when the stream is missing, unreadable or lacks it
     */
    static String read(final @Nullable InputStream stream) {
        if (stream == null) {
            return UNKNOWN;
        }
        try (InputStream in = stream) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty(VERSION_KEY, UNKNOWN).trim();
        } catch (final IOException e) {
            return UNKNOWN;
        }
    }
}
