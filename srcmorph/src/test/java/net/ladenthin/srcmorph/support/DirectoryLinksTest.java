// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.CommonTestFixtures;
import org.junit.jupiter.api.Test;

public class DirectoryLinksTest {

    private final DirectoryLinks links = new DirectoryLinks();

    // <editor-fold defaultstate="collapsed" desc="isLink">
    @Test
    public void isLink_aDirectory_isNoLink() throws Exception {
        // arrange
        final Path directory = Files.createTempDirectory("ai-index-test");

        // act, assert
        assertThat(links.isLink(directory, attributesOf(directory)), is(false));
    }

    @Test
    public void isLink_aSymbolicLink_isALink() throws Exception {
        // arrange: the attributes alone say it
        final Path directory = Files.createTempDirectory("ai-index-test");

        // act, assert
        assertThat(links.isLink(directory, attributes(true, false)), is(true));
    }

    @Test
    public void isLink_anOtherDirectoryWhoseRealPathIsItsOwn_isNoLink() throws Exception {
        // arrange: a directory with a reparse point that links nowhere, a cloud file placeholder say
        final Path directory = Files.createTempDirectory("ai-index-test").resolve("plain");
        Files.createDirectory(directory);

        // act, assert
        assertThat(links.isLink(directory, attributes(false, true)), is(false));
    }

    @Test
    public void isLink_anOtherDirectoryWhoseRealPathIsAnother_isALink() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path target = Files.createDirectory(temp.resolve("target"));
        final Path link = temp.resolve("link");
        CommonTestFixtures.directoryLink(link, target);

        // act, assert
        assertThat(links.isLink(link, attributes(false, true)), is(true));
    }

    @Test
    public void isLink_anOtherDirectoryThatIsGone_isALink() throws Exception {
        // arrange: a junction whose target is gone has no real path
        final Path gone = Files.createTempDirectory("ai-index-test").resolve("gone");

        // act, assert
        assertThat(links.isLink(gone, attributes(false, true)), is(true));
    }

    @Test
    public void isLink_aFileSystemRoot_isNoLink() throws Exception {
        // arrange
        final Path root =
                Files.createTempDirectory("ai-index-test").toAbsolutePath().getRoot();

        // act, assert
        assertThat(links.isLink(root, attributes(false, true)), is(false));
    }

    @Test
    public void isLink_aJunction_isALink() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path target = Files.createDirectory(temp.resolve("target"));
        final Path junction = temp.resolve("junction");
        CommonTestFixtures.junction(junction, target);

        // act, assert: read as a walk reads it
        assertThat(links.isLink(junction, attributesOf(junction)), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isLinkToADirectory">
    @Test
    public void isLinkToADirectory_aDirectoryOrAFileOrAPathThatIsGone_isNone() throws Exception {
        // arrange
        final Path directory = Files.createTempDirectory("ai-index-test");
        final Path file = Files.write(directory.resolve("A.java"), new byte[0]);

        // act, assert
        assertThat(links.isLinkToADirectory(directory), is(false));
        assertThat(links.isLinkToADirectory(file), is(false));
        assertThat(links.isLinkToADirectory(directory.resolve("gone")), is(false));
    }

    @Test
    public void isLinkToADirectory_aLinkToADirectory_isOne() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path target = Files.createDirectory(temp.resolve("target"));
        final Path link = temp.resolve("link");
        CommonTestFixtures.directoryLink(link, target);

        // act, assert
        assertThat(links.isLinkToADirectory(link), is(true));
    }

    @Test
    public void isLinkToADirectory_aJunctionWhoseTargetIsGone_isOne() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path target = Files.createDirectory(temp.resolve("target"));
        final Path junction = temp.resolve("junction");
        CommonTestFixtures.junction(junction, target);
        Files.delete(target);

        // act, assert
        assertThat(links.isLinkToADirectory(junction), is(true));
    }

    @Test
    public void isLinkToADirectory_aSymbolicLink_isOneWhenItsTargetIsADirectory() throws Exception {
        // arrange: the attributes say symbolic link, the path is what it points at
        final Path directory = Files.createTempDirectory("ai-index-test");
        final Path file = Files.write(directory.resolve("A.java"), new byte[0]);

        // act, assert
        assertThat(links.isLinkToADirectory(directory, attributes(true, false)), is(true));
        assertThat(links.isLinkToADirectory(file, attributes(true, false)), is(false));
    }

    @Test
    public void isLinkToADirectory_anOtherDirectoryWhoseRealPathIsItsOwn_isNone() throws Exception {
        // arrange
        final Path directory = Files.createTempDirectory("ai-index-test").resolve("plain");
        Files.createDirectory(directory);

        // act, assert
        assertThat(links.isLinkToADirectory(directory, attributes(false, true)), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="walkFileTree">
    @Test
    public void walkFileTree_aStartShownAsAnotherPath_reportsEveryPathBelowIt() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path start = Files.createDirectories(temp.resolve("real/a"));
        Files.write(start.resolve("A.java"), new byte[0]);
        Files.createDirectory(temp.resolve("real/skipped"));
        Files.write(temp.resolve("real/skipped/S.java"), new byte[0]);
        final Path locked = Files.createDirectory(temp.resolve("real/locked"));
        final Path shown = temp.resolve("alias");
        final List<String> events = new ArrayList<>();

        // act
        try (AutoCloseable denied = CommonTestFixtures.unlistable(locked)) {
            links.walkFileTree(shown, temp.resolve("real"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path directory, final BasicFileAttributes attributes) {
                    events.add("enter " + name(shown, directory));
                    return directory.endsWith("skipped") ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) {
                    events.add("file " + name(shown, file));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                    events.add("failed " + name(shown, file));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(final Path directory, final IOException e) {
                    events.add("leave " + name(shown, directory));
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        // assert: in the walk's order, which the file system gives; every path below the shown one
        Collections.sort(events);
        assertThat(
                events,
                is(Arrays.asList(
                        "enter ", "enter a", "enter skipped", "failed locked", "file a/A.java", "leave ", "leave a")));
    }

    /** Returns a path below {@code shown}, with {@code /} separators; fails for any other path. */
    private static String name(final Path shown, final Path path) {
        assertThat(path.startsWith(shown), is(true));
        return shown.relativize(path).toString().replace('\\', '/');
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isDirectoryNoLink">
    @Test
    public void isDirectoryNoLink_aDirectory_isOne() throws Exception {
        // arrange
        final Path directory = Files.createTempDirectory("ai-index-test");

        // act, assert
        assertThat(links.isDirectoryNoLink(directory), is(true));
    }

    @Test
    public void isDirectoryNoLink_aFile_isNone() throws Exception {
        // arrange
        final Path file = Files.createTempFile("ai-index-test", ".java");

        // act, assert
        assertThat(links.isDirectoryNoLink(file), is(false));
    }

    @Test
    public void isDirectoryNoLink_aPathThatIsGone_isNone() throws Exception {
        // arrange
        final Path gone = Files.createTempDirectory("ai-index-test").resolve("gone");

        // act, assert
        assertThat(links.isDirectoryNoLink(gone), is(false));
    }

    @Test
    public void isDirectoryNoLink_aLinkToADirectory_isNone() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path target = Files.createDirectory(temp.resolve("target"));
        final Path link = temp.resolve("link");
        CommonTestFixtures.directoryLink(link, target);

        // act, assert
        assertThat(links.isDirectoryNoLink(link), is(false));
    }
    // </editor-fold>

    private static BasicFileAttributes attributesOf(final Path path) throws Exception {
        return Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
    }

    /** The attributes of a directory, a symbolic link or "other" as given. */
    private static BasicFileAttributes attributes(final boolean symbolicLink, final boolean other) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                return FileTime.fromMillis(0L);
            }

            @Override
            public FileTime lastAccessTime() {
                return FileTime.fromMillis(0L);
            }

            @Override
            public FileTime creationTime() {
                return FileTime.fromMillis(0L);
            }

            @Override
            public boolean isRegularFile() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return !symbolicLink;
            }

            @Override
            public boolean isSymbolicLink() {
                return symbolicLink;
            }

            @Override
            public boolean isOther() {
                return other;
            }

            @Override
            public long size() {
                return 0L;
            }

            @Override
            public Object fileKey() {
                return "";
            }
        };
    }
}
