// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import lombok.ToString;

/**
 * Tells a link to a directory from a directory, so a walk enters no directory twice and runs into no
 * loop: a symbolic link, and on Windows a junction, which the attributes read without following links
 * report as a directory that is also "other". A directory with another reparse point (a cloud file
 * placeholder, say) is "other" as well, but no link: its real path is its own.
 */
@ToString
public class DirectoryLinks {

    /** Creates a new {@link DirectoryLinks}. */
    public DirectoryLinks() {
        // no-op
    }

    /**
     * Returns whether a directory a walk reached is a link to another directory.
     *
     * @param directory  the directory
     * @param attributes its attributes, read without following links
     * @return {@code true} for a symbolic link, and for an "other" directory whose real path is not its
     *         own or cannot be read; {@code false} for every other directory
     */
    public boolean isLink(final Path directory, final BasicFileAttributes attributes) {
        if (attributes.isSymbolicLink()) {
            return true;
        }
        if (!attributes.isOther()) {
            return false;
        }
        final Path absolute = directory.toAbsolutePath();
        final Path parent = absolute.getParent();
        final Path name = absolute.getFileName();
        if (parent == null || name == null) {
            // a file system root links nowhere
            return false;
        }
        try {
            return !absolute.toRealPath().equals(parent.toRealPath().resolve(name));
        } catch (final IOException e) {
            // a junction whose target is gone
            return true;
        }
    }

    /**
     * Returns whether a path is a directory and no link to one ({@link #isLink(Path, BasicFileAttributes)}).
     *
     * @param path the path
     * @return {@code true} for a directory a walk enters; {@code false} for a link, a file, and a path that
     *         cannot be read
     */
    public boolean isDirectoryNoLink(final Path path) {
        final BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (final IOException e) {
            return false;
        }
        return attributes.isDirectory() && !isLink(path, attributes);
    }
}
