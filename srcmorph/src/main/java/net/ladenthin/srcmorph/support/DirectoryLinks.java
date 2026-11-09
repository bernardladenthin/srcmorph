// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

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
     * Returns whether a path is a link to a directory ({@link #isLinkToADirectory(Path, BasicFileAttributes)}),
     * its attributes read without following links: a walk that could not open it reports a junction whose
     * target is gone as a failed read, not as a directory.
     *
     * @param path the path
     * @return {@code true} for a link to a directory; {@code false} otherwise, and when the attributes cannot
     *         be read
     */
    public boolean isLinkToADirectory(final Path path) {
        final BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (final IOException e) {
            return false;
        }
        return isLinkToADirectory(path, attributes);
    }

    /**
     * Returns whether a path is a link to a directory: a symbolic link whose target is a directory, or a
     * link {@link #isLink(Path, BasicFileAttributes)} tells from its attributes (a junction, also one whose
     * target is gone).
     *
     * @param path       the path
     * @param attributes its attributes, read without following links
     * @return {@code true} for a link to a directory
     */
    boolean isLinkToADirectory(final Path path, final BasicFileAttributes attributes) {
        if (attributes.isSymbolicLink()) {
            return Files.isDirectory(path);
        }
        return isLink(path, attributes);
    }

    /**
     * Walks {@code start} without following links ({@link Files#walkFileTree(Path, FileVisitor)}) and
     * reports every path to {@code visitor} as the same path below {@code shown}: a walk of a root that is
     * a symbolic link walks the link's target, as one that does not follow links would take the link for a
     * file and not enter it, and still names every path by the link.
     *
     * @param shown   the path the visitor sees for {@code start}
     * @param start   the directory walked
     * @param visitor the visitor
     * @throws IOException if the visitor throws one, as {@link Files#walkFileTree(Path, FileVisitor)} does
     */
    public void walkFileTree(final Path shown, final Path start, final FileVisitor<Path> visitor) throws IOException {
        Files.walkFileTree(start, new ShownVisitor(shown, start, visitor));
    }

    /** Hands every path of a walk of {@code start} on as the same path below {@code shown}. */
    private static final class ShownVisitor implements FileVisitor<Path> {

        /** The path the visitor sees for {@link #start}. */
        private final Path shown;

        /** The directory walked. */
        private final Path start;

        /** The visitor the paths are handed on to. */
        private final FileVisitor<Path> visitor;

        ShownVisitor(final Path shown, final Path start, final FileVisitor<Path> visitor) {
            this.shown = shown;
            this.start = start;
            this.visitor = visitor;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path directory, final BasicFileAttributes attributes)
                throws IOException {
            return visitor.preVisitDirectory(shown(directory), attributes);
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
            return visitor.visitFile(shown(file), attributes);
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException e) throws IOException {
            return visitor.visitFileFailed(shown(file), e);
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path directory, final @Nullable IOException e)
                throws IOException {
            return visitor.postVisitDirectory(shown(directory), e);
        }

        private Path shown(final Path path) {
            return shown.resolve(start.relativize(path));
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
