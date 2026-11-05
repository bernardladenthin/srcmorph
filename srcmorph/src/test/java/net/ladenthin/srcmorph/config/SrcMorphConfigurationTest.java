// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Arrays;
import net.ladenthin.srcmorph.support.GeneratorVersion;
import org.junit.jupiter.api.Test;

public class SrcMorphConfigurationTest {

    @Test
    public void defaults_matchDocumentedConstants() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        assertThat(c.getOutputDirectory(), is(new File(SrcMorphConfiguration.DEFAULT_OUTPUT_DIRECTORY)));
        assertThat(c.isForce(), is(false));
        assertThat(c.isPlanOnly(), is(false));
        assertThat(c.getGenerationProvider(), is(SrcMorphConfiguration.DEFAULT_GENERATION_PROVIDER));
        assertThat(c.getAiVersion(), is(SrcMorphConfiguration.DEFAULT_AI_VERSION));
        assertThat(c.getMinFileSizeBytes(), is(0L));
        assertThat(c.getMaxFileSizeBytes(), is(0L));
        assertThat(c.getGeneratorVersion(), is(new GeneratorVersion().version()));
    }

    @Test
    public void nullableCollectionsAndStringsAreNullByDefault() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        assertThat(c.getSubtrees(), is(nullValue()));
        assertThat(c.getExcludes(), is(nullValue()));
        assertThat(c.getFileExtensions(), is(nullValue()));
        assertThat(c.getPromptDefinitions(), is(nullValue()));
        assertThat(c.getAiDefinitions(), is(nullValue()));
        assertThat(c.getFieldGenerations(), is(nullValue()));
        assertThat(c.getFactDefinitions(), is(nullValue()));
        assertThat(c.getModelDirectory(), is(nullValue()));
        assertThat(c.getProfileDirectory(), is(nullValue()));
        assertThat(c.getProjectName(), is(nullValue()));
    }

    @Test
    public void modelDirectoryRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/models");
        c.setModelDirectory(dir);
        assertThat(c.getModelDirectory(), is(sameInstance(dir)));
    }

    @Test
    public void profileDirectoryRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("prompts");
        c.setProfileDirectory(dir);
        assertThat(c.getProfileDirectory(), is(sameInstance(dir)));
    }

    @Test
    public void baseDirectoryRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/base");
        c.setBaseDirectory(dir);
        assertThat(c.getBaseDirectory(), is(sameInstance(dir)));
    }

    @Test
    public void outputDirectoryRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/out");
        c.setOutputDirectory(dir);
        assertThat(c.getOutputDirectory(), is(sameInstance(dir)));
    }

    @Test
    public void forceRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setForce(true);
        assertThat(c.isForce(), is(true));
    }

    @Test
    public void fileInterpretersAndContentHeadChars_defaultAndRoundTrip() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        assertThat(c.getFileInterpreters() == null, is(true));
        assertThat(c.getContentHeadChars(), is(SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS));
        c.setFileInterpreters(java.util.Arrays.asList("bash", "perl"));
        c.setContentHeadChars(65_536);
        assertThat(c.getFileInterpreters(), is(java.util.Arrays.asList("bash", "perl")));
        assertThat(c.getContentHeadChars(), is(65_536));
    }

    @Test
    public void regenerateOn_everySwitchOnByDefault_andNullTurnsThemOnAgain() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final RegenerateOn defaults = c.getRegenerateOn();
        assertThat(defaults.isSource(), is(true));
        assertThat(defaults.isPrompt(), is(true));
        assertThat(defaults.isFacts(), is(true));
        assertThat(defaults.isModel(), is(true));
        assertThat(defaults.isChildren(), is(true));

        final RegenerateOn frozen = new RegenerateOn();
        frozen.setSource(false);
        frozen.setPrompt(false);
        frozen.setFacts(false);
        frozen.setModel(false);
        frozen.setChildren(false);
        c.setRegenerateOn(frozen);
        assertThat(c.getRegenerateOn().isSource(), is(false));
        assertThat(c.getRegenerateOn().isPrompt(), is(false));
        assertThat(c.getRegenerateOn().isFacts(), is(false));
        assertThat(c.getRegenerateOn().isModel(), is(false));
        assertThat(c.getRegenerateOn().isChildren(), is(false));

        c.setRegenerateOn(null);
        assertThat(c.getRegenerateOn().isPrompt(), is(true));
    }

    @Test
    public void planOnlyRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setPlanOnly(true);
        assertThat(c.isPlanOnly(), is(true));
    }

    @Test
    public void subtreesRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setSubtrees(Arrays.asList("src/main/java/a"));
        assertThat(c.getSubtrees(), hasItem("src/main/java/a"));
    }

    @Test
    public void excludesRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setExcludes(Arrays.asList("**/generated/**"));
        assertThat(c.getExcludes(), hasItem("**/generated/**"));
    }

    @Test
    public void fileExtensionsRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setFileExtensions(Arrays.asList(".kt"));
        assertThat(c.getFileExtensions(), hasItem(".kt"));
    }

    @Test
    public void minFileSizeBytesRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setMinFileSizeBytes(123L);
        assertThat(c.getMinFileSizeBytes(), is(123L));
    }

    @Test
    public void maxFileSizeBytesRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setMaxFileSizeBytes(456L);
        assertThat(c.getMaxFileSizeBytes(), is(456L));
    }

    @Test
    public void generationProviderRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setGenerationProvider("llamacpp-jni");
        assertThat(c.getGenerationProvider(), is("llamacpp-jni"));
    }

    @Test
    public void promptDefinitionsRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final PromptDefinition p = new PromptDefinition();
        p.setKey("k");
        p.setTemplate("t");
        c.setPromptDefinitions(Arrays.asList(p));
        assertThat(c.getPromptDefinitions(), hasItem(p));
    }

    @Test
    public void aiDefinitionsRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final ModelDefinition m = new ModelDefinition();
        m.setKey("m");
        c.setAiDefinitions(Arrays.asList(m));
        assertThat(c.getAiDefinitions(), hasItem(m));
    }

    @Test
    public void fieldGenerationsRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final RoutingRule f = new RoutingRule();
        f.setPromptKey("p");
        c.setFieldGenerations(Arrays.asList(f));
        assertThat(c.getFieldGenerations(), hasItem(f));
    }

    @Test
    public void factDefinitionsRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final FactDefinition fd = new FactDefinition();
        fd.setKey("fd");
        c.setFactDefinitions(Arrays.asList(fd));
        assertThat(c.getFactDefinitions(), hasItem(fd));
    }

    @Test
    public void generatorVersionRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setGeneratorVersion("1.2.3");
        assertThat(c.getGeneratorVersion(), is("1.2.3"));
    }

    @Test
    public void aiVersionRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setAiVersion("2.0.0");
        assertThat(c.getAiVersion(), is("2.0.0"));
    }

    @Test
    public void projectNameRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setProjectName("my-project");
        assertThat(c.getProjectName(), is("my-project"));
    }

    @Test
    public void toStringContainsDistinguishingField() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setGenerationProvider("llamacpp-jni");
        assertThat(c.toString(), org.hamcrest.CoreMatchers.containsString("llamacpp-jni"));
    }

    @Test
    public void runRecordFile_defaultsNullAndRoundTrips() {
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        org.hamcrest.MatcherAssert.assertThat(c.getRunRecordFile(), org.hamcrest.CoreMatchers.nullValue());
        c.setRunRecordFile(new java.io.File("run.json"));
        org.hamcrest.MatcherAssert.assertThat(
                c.getRunRecordFile(), org.hamcrest.CoreMatchers.is(new java.io.File("run.json")));
    }
}
