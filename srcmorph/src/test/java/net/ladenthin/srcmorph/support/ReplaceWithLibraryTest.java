// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

package net.ladenthin.srcmorph.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Keeps the {@link ReplaceWithLibrary} bridges from outliving their reason: every marked class, method
 * or field of the production code names a library class, and the build fails once that class is on
 * the classpath, i.e. once the dependency was raised to the release that carries it. The fix is to
 * use the library class, delete the marked code and its {@code TODO.md} item; when the last one is
 * gone, this test fails too, until the annotation and the test are deleted as well.
 */
public class ReplaceWithLibraryTest {

    private static final JavaClasses PRODUCTION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("net.ladenthin.srcmorph");

    /** One marked element: where it is and what it names. */
    private static final class Bridge {
        private final String location;
        private final ReplaceWithLibrary marker;

        private Bridge(final String location, final ReplaceWithLibrary marker) {
            this.location = location;
            this.marker = marker;
        }
    }

    private static List<Bridge> bridges() {
        final List<Bridge> found = new ArrayList<>();
        for (final JavaClass type : PRODUCTION) {
            if (type.isAnnotatedWith(ReplaceWithLibrary.class)) {
                found.add(new Bridge(type.getFullName(), type.getAnnotationOfType(ReplaceWithLibrary.class)));
            }
            for (final JavaMember member : type.getMembers()) {
                if (member.isAnnotatedWith(ReplaceWithLibrary.class)) {
                    found.add(new Bridge(member.getFullName(), member.getAnnotationOfType(ReplaceWithLibrary.class)));
                }
            }
        }
        return found;
    }

    static boolean onClasspath(final String className) {
        try {
            Class.forName(className, false, ReplaceWithLibraryTest.class.getClassLoader());
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    public void everyBridge_namesALibraryClassThatIsNotThereYet() {
        final List<String> due = new ArrayList<>();
        for (final Bridge bridge : bridges()) {
            if (onClasspath(bridge.marker.value())) {
                due.add(bridge.location + " -> use " + bridge.marker.value() + " (" + bridge.marker.since() + ")");
            }
        }
        assertThat(
                "the library now carries these classes: replace the marked code with them, delete it and its"
                        + " TODO.md item",
                due,
                is(empty()));
    }

    @Test
    public void aBridgeIsLeft_otherwiseDeleteTheAnnotationAndThisTest() {
        assertThat(
                "no @ReplaceWithLibrary is left: delete ReplaceWithLibrary and ReplaceWithLibraryTest",
                bridges(),
                is(not(empty())));
    }

    /** The check itself: a class that is there is found, one that is not is not. */
    @Test
    public void onClasspath_findsWhatIsThereAndOnlyThat() {
        assertThat(onClasspath("java.lang.String"), is(true));
        assertThat(onClasspath(ReplaceWithLibrary.class.getName()), is(true));
        assertThat(onClasspath("net.ladenthin.srcmorph.NoSuchClass"), is(false));
    }
}
