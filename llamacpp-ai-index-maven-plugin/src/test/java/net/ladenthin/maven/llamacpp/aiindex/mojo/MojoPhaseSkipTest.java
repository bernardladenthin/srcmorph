// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.mojo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

/**
 * Verifies the per-phase skip mechanism: the generalized {@code shouldSkip()} rule in
 * {@link AbstractAiIndexMojo} and each concrete goal's independent phase flag.
 */
public class MojoPhaseSkipTest {

    // <editor-fold defaultstate="collapsed" desc="base shouldSkip() truth table">
    @Test
    public void shouldSkip_isGlobalSkipOrPhaseSkip() {
        // neither flag -> run
        assertThat(mojo(false, false).shouldSkip(), is(false));
        // global skip alone -> skip every phase
        assertThat(mojo(true, false).shouldSkip(), is(true));
        // phase skip alone -> skip just this phase
        assertThat(mojo(false, true).shouldSkip(), is(true));
        // both -> skip
        assertThat(mojo(true, true).shouldSkip(), is(true));
    }

    private static AbstractAiIndexMojo mojo(final boolean globalSkip, final boolean phaseSkip) {
        final AbstractAiIndexMojo mojo = new AbstractAiIndexMojo() {
            @Override
            protected int getLlamaContextSize() {
                return 0;
            }

            @Override
            protected int getLlamaThreads() {
                return 0;
            }

            @Override
            protected boolean isPhaseSkipped() {
                return phaseSkip;
            }

            @Override
            public void execute() throws MojoExecutionException {
                // no-op
            }
        };
        mojo.skip = globalSkip;
        return mojo;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="each concrete goal wires its own phase flag">
    @Test
    public void generateMojo_phaseFlagTogglesIndependently() throws Exception {
        final GenerateMojo mojo = new GenerateMojo();
        assertThat(mojo.isPhaseSkipped(), is(false));
        setBooleanField(mojo, "skipFile", true);
        assertThat(mojo.isPhaseSkipped(), is(true));
        assertThat(mojo.shouldSkip(), is(true));
    }

    @Test
    public void aggregatePackagesMojo_phaseFlagTogglesIndependently() throws Exception {
        final AggregatePackagesMojo mojo = new AggregatePackagesMojo();
        assertThat(mojo.isPhaseSkipped(), is(false));
        setBooleanField(mojo, "skipPackage", true);
        assertThat(mojo.isPhaseSkipped(), is(true));
        assertThat(mojo.shouldSkip(), is(true));
    }

    @Test
    public void aggregateProjectMojo_phaseFlagTogglesIndependently() throws Exception {
        final AggregateProjectMojo mojo = new AggregateProjectMojo();
        assertThat(mojo.isPhaseSkipped(), is(false));
        setBooleanField(mojo, "skipProject", true);
        assertThat(mojo.isPhaseSkipped(), is(true));
        assertThat(mojo.shouldSkip(), is(true));
    }

    @Test
    public void globalSkipSkipsAPhaseEvenWhenItsPhaseFlagIsOff() throws Exception {
        final GenerateMojo mojo = new GenerateMojo();
        setBooleanField(mojo, "skip", true);
        assertThat(mojo.isPhaseSkipped(), is(false));
        assertThat(mojo.shouldSkip(), is(true));
    }
    // </editor-fold>

    /**
     * Sets a {@code boolean} field by name on {@code target}, walking up the class hierarchy so a
     * field declared on a superclass (e.g. the global {@code skip} on {@link AbstractAiIndexMojo}) is
     * found. Mirrors how Maven's plugin framework injects {@code @Parameter} fields via reflection.
     *
     * @param target    the object whose field to set
     * @param fieldName the field name
     * @param value     the boolean value to set
     * @throws NoSuchFieldException if no such field exists anywhere in the hierarchy
     * @throws IllegalAccessException if the field cannot be set
     */
    private static void setBooleanField(final Object target, final String fieldName, final boolean value)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                final Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setBoolean(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
