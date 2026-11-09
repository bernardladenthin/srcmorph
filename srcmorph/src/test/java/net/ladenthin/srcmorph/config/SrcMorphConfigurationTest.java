// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
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

    // <editor-fold defaultstate="collapsed" desc="defaults">
    @Test
    public void defaults_newConfiguration_matchDocumentedConstants() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act, assert
        assertThat(c.getOutputDirectory(), is(new File(SrcMorphConfiguration.DEFAULT_OUTPUT_DIRECTORY)));
        assertThat(c.isForce(), is(false));
        assertThat(c.isPlanOnly(), is(false));
        assertThat(c.isPruneOrphans(), is(SrcMorphConfiguration.DEFAULT_PRUNE_ORPHANS));
        assertThat(SrcMorphConfiguration.DEFAULT_PRUNE_ORPHANS, is(false));
        assertThat(c.getGenerationProvider(), is(SrcMorphConfiguration.DEFAULT_GENERATION_PROVIDER));
        assertThat(c.getAiVersion(), is(SrcMorphConfiguration.DEFAULT_AI_VERSION));
        assertThat(c.getMinFileSizeBytes(), is(0L));
        assertThat(c.getMaxFileSizeBytes(), is(0L));
        assertThat(c.getGeneratorVersion(), is(new GeneratorVersion().version()));
    }

    @Test
    public void defaults_newConfiguration_nullableCollectionsAndStringsAreNull() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act, assert
        assertThat(c.getSubtrees(), is(nullValue()));
        assertThat(c.getExcludes(), is(nullValue()));
        assertThat(c.getFileExtensions(), is(nullValue()));
        assertThat(c.getPromptDefinitions(), is(nullValue()));
        assertThat(c.getAiDefinitions(), is(nullValue()));
        assertThat(c.getRules(), is(nullValue()));
        assertThat(c.getFactDefinitions(), is(nullValue()));
        assertThat(c.getModelDirectory(), is(nullValue()));
        assertThat(c.getProfileDirectory(), is(nullValue()));
        assertThat(c.getProjectName(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="modelDirectory">
    @Test
    public void modelDirectory_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/models");

        // act
        c.setModelDirectory(dir);

        // assert
        assertThat(c.getModelDirectory(), is(sameInstance(dir)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="profileDirectory">
    @Test
    public void profileDirectory_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("prompts");

        // act
        c.setProfileDirectory(dir);

        // assert
        assertThat(c.getProfileDirectory(), is(sameInstance(dir)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="baseDirectory">
    @Test
    public void baseDirectory_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/base");

        // act
        c.setBaseDirectory(dir);

        // assert
        assertThat(c.getBaseDirectory(), is(sameInstance(dir)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="outputDirectory">
    @Test
    public void outputDirectory_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final File dir = new File("/tmp/out");

        // act
        c.setOutputDirectory(dir);

        // assert
        assertThat(c.getOutputDirectory(), is(sameInstance(dir)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="force">
    @Test
    public void force_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setForce(true);

        // assert
        assertThat(c.isForce(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="pruneOrphans">
    @Test
    public void pruneOrphans_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setPruneOrphans(true);

        // assert
        assertThat(c.isPruneOrphans(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileInterpretersAndContentHeadChars">
    @Test
    public void fileInterpretersAndContentHeadChars_defaultAndSet_defaultAndRoundTrip() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // pre-assert
        assertThat(c.getFileInterpreters() == null, is(true));
        assertThat(c.getContentHeadChars(), is(SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS));

        // act
        c.setFileInterpreters(Arrays.asList("bash", "perl"));
        c.setContentHeadChars(65_536);

        // assert
        assertThat(c.getFileInterpreters(), is(Arrays.asList("bash", "perl")));
        assertThat(c.getContentHeadChars(), is(65_536));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="regenerateOn">
    @Test
    public void regenerateOn_defaultSetAndNull_everySwitchOnByDefaultAndNullTurnsThemOnAgain() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // pre-assert
        final RegenerateOn defaults = c.getRegenerateOn();
        assertThat(defaults.isSource(), is(true));
        assertThat(defaults.isPrompt(), is(true));
        assertThat(defaults.isFacts(), is(true));
        assertThat(defaults.isModel(), is(true));
        assertThat(defaults.isChildren(), is(true));

        // act
        final RegenerateOn frozen = new RegenerateOn();
        frozen.setSource(false);
        frozen.setPrompt(false);
        frozen.setFacts(false);
        frozen.setModel(false);
        frozen.setChildren(false);
        c.setRegenerateOn(frozen);

        // assert
        assertThat(c.getRegenerateOn().isSource(), is(false));
        assertThat(c.getRegenerateOn().isPrompt(), is(false));
        assertThat(c.getRegenerateOn().isFacts(), is(false));
        assertThat(c.getRegenerateOn().isModel(), is(false));
        assertThat(c.getRegenerateOn().isChildren(), is(false));

        // act
        c.setRegenerateOn(null);

        // assert
        assertThat(c.getRegenerateOn().isPrompt(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="planOnly">
    @Test
    public void planOnly_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setPlanOnly(true);

        // assert
        assertThat(c.isPlanOnly(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="subtrees">
    @Test
    public void subtrees_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setSubtrees(Arrays.asList("src/main/java/a"));

        // assert
        assertThat(c.getSubtrees(), hasItem("src/main/java/a"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="excludes">
    @Test
    public void excludes_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setExcludes(Arrays.asList("**/generated/**"));

        // assert
        assertThat(c.getExcludes(), hasItem("**/generated/**"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileExtensions">
    @Test
    public void fileExtensions_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setFileExtensions(Arrays.asList(".kt"));

        // assert
        assertThat(c.getFileExtensions(), hasItem(".kt"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="minFileSizeBytes">
    @Test
    public void minFileSizeBytes_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setMinFileSizeBytes(123L);

        // assert
        assertThat(c.getMinFileSizeBytes(), is(123L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="maxFileSizeBytes">
    @Test
    public void maxFileSizeBytes_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setMaxFileSizeBytes(456L);

        // assert
        assertThat(c.getMaxFileSizeBytes(), is(456L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generationProvider">
    @Test
    public void generationProvider_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setGenerationProvider("llamacpp-jni");

        // assert
        assertThat(c.getGenerationProvider(), is("llamacpp-jni"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="promptDefinitions">
    @Test
    public void promptDefinitions_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final PromptDefinition p = new PromptDefinition();
        p.setKey("k");
        p.setTemplate("t");

        // act
        c.setPromptDefinitions(Arrays.asList(p));

        // assert
        assertThat(c.getPromptDefinitions(), hasItem(p));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aiDefinitions">
    @Test
    public void aiDefinitions_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final ModelDefinition m = new ModelDefinition();
        m.setKey("m");

        // act
        c.setAiDefinitions(Arrays.asList(m));

        // assert
        assertThat(c.getAiDefinitions(), hasItem(m));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="rules">
    @Test
    public void rules_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final RoutingRule f = new RoutingRule();
        f.setPromptKey("p");

        // act
        c.setRules(Arrays.asList(f));

        // assert
        assertThat(c.getRules(), hasItem(f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factDefinitions">
    @Test
    public void factDefinitions_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        final FactDefinition fd = new FactDefinition();
        fd.setKey("fd");

        // act
        c.setFactDefinitions(Arrays.asList(fd));

        // assert
        assertThat(c.getFactDefinitions(), hasItem(fd));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generatorVersion">
    @Test
    public void generatorVersion_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setGeneratorVersion("1.2.3");

        // assert
        assertThat(c.getGeneratorVersion(), is("1.2.3"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aiVersion">
    @Test
    public void aiVersion_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setAiVersion("2.0.0");

        // assert
        assertThat(c.getAiVersion(), is("2.0.0"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="projectName">
    @Test
    public void projectName_set_roundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // act
        c.setProjectName("my-project");

        // assert
        assertThat(c.getProjectName(), is("my-project"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_generationProviderSet_containsDistinguishingField() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();
        c.setGenerationProvider("llamacpp-jni");

        // act, assert
        assertThat(c.toString(), containsString("llamacpp-jni"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="runRecordFile">
    @Test
    public void runRecordFile_defaultAndSet_defaultsNullAndRoundTrips() {
        // arrange
        final SrcMorphConfiguration c = new SrcMorphConfiguration();

        // pre-assert
        assertThat(c.getRunRecordFile(), nullValue());

        // act
        c.setRunRecordFile(new File("run.json"));

        // assert
        assertThat(c.getRunRecordFile(), is(new File("run.json")));
    }
    // </editor-fold>
}
