// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.FactDefinition;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.OversizeStrategy;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.provider.ProviderConfig;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

public class EngineSupportTest {

    private ListAppender<ILoggingEvent> logAppender;

    /** The logger's level before the test, restored after it (null: inherited). */
    private Level previousLevel;

    @BeforeEach
    public void attachLogAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(EngineSupport.class);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.DEBUG);
        logger.addAppender(logAppender);
    }

    @AfterEach
    public void detachLogAppender() {
        final Logger logger = (Logger) LoggerFactory.getLogger(EngineSupport.class);
        logger.detachAppender(logAppender);
        logger.setLevel(previousLevel);
        logAppender.stop();
    }

    @TempDir
    Path tempDir;

    // <editor-fold defaultstate="collapsed" desc="resolveSubtrees">
    @Test
    public void resolveSubtrees_null_returnsEmpty() throws SrcMorphException {
        // act, assert
        assertThat(EngineSupport.resolveSubtrees(tempDir, null), is(Collections.<Path>emptyList()));
    }

    @Test
    public void resolveSubtrees_empty_returnsEmpty() throws SrcMorphException {
        // act, assert
        assertThat(
                EngineSupport.resolveSubtrees(tempDir, Collections.<String>emptyList()),
                is(Collections.<Path>emptyList()));
    }

    @Test
    public void resolveSubtrees_earlyReturn_resultIsMutable() throws SrcMorphException {
        // arrange
        // The early-return (null/empty subtrees) branch returns the same mutable ArrayList as the
        // loop-completion branch, not an immutable Collections.emptyList(), so callers can treat every
        // result uniformly. Mutating it here must not throw UnsupportedOperationException.
        final List<Path> resolved = EngineSupport.resolveSubtrees(tempDir, null);

        // act
        resolved.add(tempDir);

        // assert
        assertThat(resolved, hasItem(tempDir));
    }

    @Test
    public void resolveSubtrees_existingSubtree_isResolved() throws IOException, SrcMorphException {
        // arrange
        final Path sub = tempDir.resolve("exists");
        Files.createDirectories(sub);

        // act
        final List<Path> resolved = EngineSupport.resolveSubtrees(tempDir, Arrays.asList("exists"));

        // assert
        assertThat(resolved, hasItem(sub.normalize()));
    }

    @Test
    public void resolveSubtrees_aSubtreeInsideAnotherOrTwice_isLeftOut() throws IOException, SrcMorphException {
        // arrange
        // without this, every file below src/main/java would be collected twice and fail the run
        for (final String directory : new String[] {"src/main/java", "src/test", "lib"}) {
            Files.createDirectories(tempDir.resolve(directory));
        }

        // act
        final List<Path> resolved =
                EngineSupport.resolveSubtrees(tempDir, Arrays.asList("src/main/java", "src", "lib", "src/test", "lib"));

        // assert
        assertThat(resolved, is(Arrays.asList(tempDir.resolve("src"), tempDir.resolve("lib"))));
    }

    @Test
    public void resolveSubtrees_aSubtreeBehindALinkInsideAnother_isKept() throws Exception {
        // arrange: src/shared and src/deep/lib lie behind links, which the walk of src does not enter
        Files.createDirectories(tempDir.resolve("shared/lib"));
        Files.createDirectories(tempDir.resolve("src/deep"));
        CommonTestFixtures.directoryLink(tempDir.resolve("src/shared"), tempDir.resolve("shared"));
        CommonTestFixtures.directoryLink(tempDir.resolve("src/deep/lib"), tempDir.resolve("shared/lib"));

        // act
        final List<Path> resolved = EngineSupport.resolveSubtrees(
                tempDir, Arrays.asList("src/shared", "src", "src/deep/lib", "src/shared/lib"));

        // assert: src/shared/lib lies in src/shared, behind no further link
        assertThat(
                resolved,
                is(Arrays.asList(
                        tempDir.resolve("src/shared"), tempDir.resolve("src"), tempDir.resolve("src/deep/lib"))));
    }

    @Test
    public void resolveSubtrees_missingSubtree_isRefused() {
        // arrange
        final List<String> subtrees = Arrays.asList("does-not-exist");

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> EngineSupport.resolveSubtrees(tempDir, subtrees));

        // assert
        assertThat(thrown.getMessage(), containsString("Subtree 'does-not-exist' does not exist: "));
    }

    @Test
    public void resolveSubtrees_aFileInsteadOfADirectory_isRefused() throws IOException {
        // arrange
        Files.write(tempDir.resolve("plain.txt"), new byte[0]);
        final List<String> subtrees = Arrays.asList("plain.txt");

        // act, assert
        assertThrows(SrcMorphException.class, () -> EngineSupport.resolveSubtrees(tempDir, subtrees));
    }

    @Test
    public void resolveSubtrees_outsideTheBaseDirectory_isRefused() throws IOException {
        // arrange
        final Path base = Files.createDirectories(tempDir.resolve("base"));
        Files.createDirectories(tempDir.resolve("other"));
        final List<String> subtrees = Arrays.asList("../other");

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> EngineSupport.resolveSubtrees(base, subtrees));

        // assert
        assertThat(thrown.getMessage(), containsString("outside the base directory"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildPromptSupport">
    @Test
    public void buildPromptSupport_nullList_buildsEmptySupport() throws SrcMorphException {
        // act
        final PromptCatalog support = EngineSupport.buildPromptSupport(null, null, null);

        // assert
        assertThat(support, is(notNullValue()));
    }

    @Test
    public void buildPromptSupport_missingKey_throwsSrcMorphException() {
        // arrange
        final PromptDefinition bad = new PromptDefinition();
        bad.setTemplate("template with no key");
        final List<PromptDefinition> definitions = Arrays.asList(bad);

        // act
        final SrcMorphException e =
                assertThrows(SrcMorphException.class, () -> EngineSupport.buildPromptSupport(definitions, null, null));

        // assert
        assertThat(e.getMessage(), containsString("Invalid configuration:"));
    }

    @Test
    public void buildPromptSupport_templateFile_isReadFromTheProfileDirectory() throws Exception {
        // arrange
        Files.write(tempDir.resolve("file-body-java.md"), "You are a Java code indexer.".getBytes(UTF_8));
        final PromptDefinition fromFile = new PromptDefinition();
        fromFile.setKey("file-body-java");
        fromFile.setTemplateFile("file-body-java.md");

        // act
        final PromptCatalog support = EngineSupport.buildPromptSupport(Arrays.asList(fromFile), tempDir.toFile(), null);

        // assert
        assertThat(support.systemPrompt("file-body-java"), is("You are a Java code indexer."));
    }

    @Test
    public void buildPromptSupport_aRulesMapAndReducePrompts_areReadFromTheProfileDirectory() throws Exception {
        // arrange
        Files.write(tempDir.resolve("file-body-java.md"), "BODY".getBytes(UTF_8));
        Files.write(tempDir.resolve("map-chunk.md"), "MAP".getBytes(UTF_8));
        Files.write(tempDir.resolve("reduce-parts.md"), "REDUCE".getBytes(UTF_8));
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("file-body-java");
        rule.setMapPromptKey("map-chunk");
        rule.setReducePromptKey("reduce-parts");
        final RoutingRule plain = new RoutingRule();
        plain.setPromptKey("file-body-java");

        // act
        final PromptCatalog support =
                EngineSupport.buildPromptSupport(null, tempDir.toFile(), Arrays.asList(rule, plain));

        // assert
        assertThat(support.systemPrompt("map-chunk"), is("MAP"));
        assertThat(support.systemPrompt("reduce-parts"), is("REDUCE"));
        assertThat(support.systemPrompt("file-body-java+map-chunk"), is("BODY\n\nMAP"));
    }

    @Test
    public void buildPromptSupport_missingTemplateFile_throwsSrcMorphException() {
        // arrange
        final PromptDefinition fromFile = new PromptDefinition();
        fromFile.setKey("file-body-java");
        fromFile.setTemplateFile("missing.md");
        final List<PromptDefinition> definitions = Arrays.asList(fromFile);
        final File profileDirectory = tempDir.toFile();

        // act
        final SrcMorphException e = assertThrows(
                SrcMorphException.class, () -> EngineSupport.buildPromptSupport(definitions, profileDirectory, null));

        // assert
        assertThat(e.getMessage(), containsString("Invalid configuration: promptDefinitions[0]"));
        assertThat(e.getMessage(), containsString("missing.md cannot be read"));
    }

    @Test
    public void buildPromptSupport_templateAndTemplateFile_throwsSrcMorphException() {
        // arrange
        final PromptDefinition both = new PromptDefinition();
        both.setKey("file-body-java");
        both.setTemplate("inline");
        both.setTemplateFile("file.md");
        final List<PromptDefinition> definitions = Arrays.asList(both);

        // act
        final SrcMorphException e =
                assertThrows(SrcMorphException.class, () -> EngineSupport.buildPromptSupport(definitions, null, null));

        // assert
        assertThat(e.getMessage(), containsString("sets both template and templateFile"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="basePath">
    @Test
    public void basePath_isTheBaseDirectory_absoluteAndNormalized() throws SrcMorphException {
        // arrange
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(new File("a/../b"));

        // act
        final Path base = EngineSupport.basePath(config);

        // assert
        assertThat(base, is(new File("b").toPath().toAbsolutePath()));
    }

    @Test
    public void basePath_unset_isAConfigurationError() {
        // arrange
        final SrcMorphConfiguration config = new SrcMorphConfiguration();

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, () -> EngineSupport.basePath(config));

        // assert
        assertThat(
                thrown.getMessage(),
                is("The configuration has no baseDirectory (its outputDirectory is "
                        + SrcMorphConfiguration.DEFAULT_OUTPUT_DIRECTORY.replace('/', File.separatorChar)
                        + "); set the directory the run reads"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildAiModelDefinitionSupport">
    @Test
    public void buildAiModelDefinitionSupport_nullList_buildsEmptySupport() throws SrcMorphException {
        // act, assert
        assertThat(EngineSupport.buildAiModelDefinitionSupport(null, null), is(notNullValue()));
    }

    @Test
    public void buildAiModelDefinitionSupport_relativeModelPath_resolvesAgainstTheModelDirectory()
            throws SrcMorphException {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("named");
        definition.setModelPath("named.gguf");
        final File modelDirectory = new File("models");

        // act
        final ModelRegistry support =
                EngineSupport.buildAiModelDefinitionSupport(Arrays.asList(definition), modelDirectory);

        // assert
        assertThat(support.definition("named").getModelPath(), is(new File(modelDirectory, "named.gguf").getPath()));
    }

    @Test
    public void buildAiModelDefinitionSupport_missingKey_throwsSrcMorphException() {
        // arrange
        final ModelDefinition bad = new ModelDefinition();
        bad.setModelPath("model.gguf");
        final List<ModelDefinition> definitions = Arrays.asList(bad);

        // act
        final SrcMorphException e = assertThrows(
                SrcMorphException.class, () -> EngineSupport.buildAiModelDefinitionSupport(definitions, null));

        // assert
        assertThat(e.getMessage(), containsString("Invalid configuration:"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requireDefinedModels">
    @Test
    public void requireDefinedModels_anUndefinedModel_namesTheRuleAndTheKey() {
        // arrange
        final RoutingRule rule = phaseRule("file-body-java", "no-such-model");
        rule.setId("java");
        final RoutingRule unnamed = phaseRule("file-body-sql", "other-missing");
        final ModelRegistry registry = new ModelRegistry(null);
        final List<RoutingRule> ruleWithId = Arrays.asList(rule);
        final List<RoutingRule> ruleWithoutId = Arrays.asList(unnamed);

        // act
        final SrcMorphException byId =
                assertThrows(SrcMorphException.class, () -> EngineSupport.requireDefinedModels(registry, ruleWithId));
        final SrcMorphException byPrompt = assertThrows(
                SrcMorphException.class, () -> EngineSupport.requireDefinedModels(registry, ruleWithoutId));

        // assert
        assertThat(
                byId.getMessage(),
                containsString("The rule 'java' names the model 'no-such-model', which no aiDefinition defines"));
        assertThat(byPrompt.getMessage(), containsString("The rule 'file-body-sql' names the model 'other-missing'"));
    }

    @Test
    public void requireDefinedModels_skipRulesAndRulesWithoutAModel_needNone() throws SrcMorphException {
        // arrange
        final RoutingRule skip = phaseRule("p", "no-such-model");
        skip.setSkip(true);
        final RoutingRule noModel = new RoutingRule();

        // act, assert -- no exception
        EngineSupport.requireDefinedModels(new ModelRegistry(null), Arrays.asList(skip, noModel, null));
    }

    @Test
    public void requireDefinedModels_aDefinedModel_passes() throws SrcMorphException {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("m");
        definition.setModelPath("m.gguf");

        // act, assert -- no exception
        EngineSupport.requireDefinedModels(
                new ModelRegistry(Arrays.asList(definition)), Arrays.asList(phaseRule("p", "m")));
    }
    // </editor-fold>

    private static RoutingRule phaseRule(final String promptKey, final String aiDefinitionKey) {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(promptKey);
        rule.setAiDefinitionKey(aiDefinitionKey);
        return rule;
    }

    // <editor-fold defaultstate="collapsed" desc="phaseRule">
    @Test
    public void phaseRule_withoutOnOversize_readsAllOfItsInputAndKeepsTheConfigurationAsGiven()
            throws SrcMorphException {
        // arrange
        final RoutingRule rule = phaseRule("package-body", "m");

        // act
        final RoutingRule required = EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule));
        final RoutingRule optional = EngineSupport.optionalPhaseRule("p", "k", Arrays.asList(rule));

        // assert
        assertThat(required.oversizeStrategy(), is(OversizeStrategy.MAP_REDUCE));
        assertThat(optional.oversizeStrategy(), is(OversizeStrategy.MAP_REDUCE));
        assertThat(rule.getOnOversize(), is(nullValue()));
    }

    @Test
    public void phaseRule_withAnOnOversize_keepsIt() throws SrcMorphException {
        // arrange
        final RoutingRule rule = phaseRule("package-body", "m");
        rule.setOnOversize("fail");

        // act, assert
        assertThat(
                EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule)).oversizeStrategy(),
                is(OversizeStrategy.FAIL));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requiredPhaseRule">
    @Test
    public void requiredPhaseRule_exactlyOne_isReturned() throws SrcMorphException {
        // arrange
        final RoutingRule rule = phaseRule("package-body", "m");

        // act
        final RoutingRule returned = EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule));

        // assert
        assertThat(returned.getPromptKey(), is("package-body"));
        assertThat(returned.getAiDefinitionKey(), is("m"));
    }

    @Test
    public void requiredPhaseRule_noneOrTwo_isRefusedNamingThePhaseAndItsKey() {
        // arrange
        for (final List<RoutingRule> rules : Arrays.asList(
                null, Collections.<RoutingRule>emptyList(), Arrays.asList(phaseRule("a", "m"), phaseRule("b", "m")))) {
            // act
            final SrcMorphException thrown = assertThrows(
                    SrcMorphException.class,
                    () -> EngineSupport.requiredPhaseRule("aggregate-packages", "packageRules", rules));

            // assert
            assertThat(thrown.getMessage(), containsString("aggregate-packages takes exactly one rule"));
            assertThat(thrown.getMessage(), containsString("packageRules in the configuration file"));
            assertThat(thrown.getMessage(), containsString((rules == null ? 0 : rules.size()) + " given"));
        }
    }

    @Test
    public void requiredPhaseRule_withoutPromptOrModel_isRefused() {
        // arrange
        for (final RoutingRule rule : Arrays.asList(phaseRule("", "m"), phaseRule("p", " "), new RoutingRule())) {
            final List<RoutingRule> rules = Arrays.asList(rule);

            // act
            final SrcMorphException thrown =
                    assertThrows(SrcMorphException.class, () -> EngineSupport.requiredPhaseRule("p", "k", rules));

            // assert
            assertThat(thrown.getMessage(), containsString("needs a promptKey and an aiDefinitionKey"));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="optionalPhaseRule">
    @Test
    public void optionalPhaseRule_none_isNull() throws SrcMorphException {
        // act, assert
        assertThat(EngineSupport.optionalPhaseRule("p", "k", null), is(nullValue()));
        assertThat(EngineSupport.optionalPhaseRule("p", "k", Collections.<RoutingRule>emptyList()), is(nullValue()));
    }

    @Test
    public void optionalPhaseRule_oneOrTwo_returnsOneAndRefusesTwo() throws SrcMorphException {
        // arrange
        final RoutingRule rule = phaseRule("project-body", "m");
        final List<RoutingRule> twoRules = Arrays.asList(rule, rule);

        // act, assert
        assertThat(
                EngineSupport.optionalPhaseRule("p", "k", Arrays.asList(rule)).getPromptKey(), is("project-body"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.optionalPhaseRule("aggregate-project", "projectRules", twoRules));

        // assert
        assertThat(thrown.getMessage(), containsString("aggregate-project takes at most one rule"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="resolveProviderConfig">
    @Test
    public void resolveProviderConfig_byKey_looksUpNamedDefinition() {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("named");
        definition.setModelPath("named.gguf");
        final ModelRegistry modelDefinitionSupport = new ModelRegistry(Arrays.asList(definition));

        // act
        final ProviderConfig result = EngineSupport.resolveProviderConfig(modelDefinitionSupport, "named");

        // assert
        assertThat(result.modelPath(), is("named.gguf"));
    }

    @Test
    public void resolveProviderConfig_missingKey_throwsIllegalArgumentException() {
        // arrange
        final ModelRegistry modelDefinitionSupport = new ModelRegistry(null);

        // act, assert
        assertThrows(
                IllegalArgumentException.class,
                () -> EngineSupport.resolveProviderConfig(modelDefinitionSupport, "missing"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validateRoutedModelPaths">

    @TempDir
    Path modelDir;

    /**
     * Builds a lookup holding one definition with the given model path.
     *
     * @param modelPath the definition's model path, may be {@code null}
     * @return the lookup
     */
    private static ModelRegistry supportWithModelPath(final String modelPath) {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setModelPath(modelPath);
        return new ModelRegistry(Arrays.asList(definition));
    }

    /**
     * Builds a lookup holding one remote definition.
     *
     * @param provider the definition's own provider
     * @param baseUrl  its base URL
     * @return the lookup
     */
    private static ModelRegistry supportWithRemote(final String provider, final String baseUrl) {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setProvider(provider);
        definition.setBaseUrl(baseUrl);
        return new ModelRegistry(Arrays.asList(definition));
    }

    /**
     * Builds a routing rule pointing at the {@code routed} definition.
     *
     * @param skip           whether the rule is a skip rule
     * @param aiDefinitionKey the definition key, may be {@code null}
     * @return the rule
     */
    private static RoutingRule routedRule(final boolean skip, final String aiDefinitionKey) {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("p1");
        rule.setAiDefinitionKey(aiDefinitionKey);
        rule.setSkip(skip);
        return rule;
    }

    @Test
    public void validateRoutedModelPaths_anUnknownProvider_isRefusedBeforeTheRun() {
        // arrange
        final ModelRegistry registry = supportWithRemote("openai-compatibel", "http://127.0.0.1:1/v1");
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class, () -> EngineSupport.validateRoutedModelPaths("mock", registry, rules));

        // assert
        assertThat(thrown.getMessage(), containsString("unknown provider 'openai-compatibel'"));
    }

    @Test
    public void validateRoutedModelPaths_theMockProvider_passes() throws SrcMorphException {
        // act, assert -- no exception
        EngineSupport.validateRoutedModelPaths(
                "mock", supportWithModelPath("absent.gguf"), Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_missingModelFile_throwsNamingTheDefinitionAndPath() {
        // arrange
        final String missing = modelDir.resolve("nope.gguf").toString();
        final ModelRegistry registry = supportWithModelPath(missing);
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_LLAMACPP_JNI, registry, rules));

        // assert -- the message must name both the definition and the path the user has to fix
        assertThat(thrown.getMessage(), containsString("routed"));
        assertThat(thrown.getMessage(), containsString("nope.gguf"));
    }

    /**
     * Writes the smallest file the GGUF reader accepts: the magic, container version 3, and zero
     * tensors / zero metadata entries. Enough to prove "this really is a GGUF" without shipping a
     * model, and deliberately declaring no context length so the config cross-check stays quiet.
     *
     * @param file where to write it
     * @throws IOException if the file cannot be written
     */
    private static void writeMinimalGguf(final Path file) throws IOException {
        final ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        header.put(new byte[] {'G', 'G', 'U', 'F'});
        header.putInt(3);
        header.putLong(0L);
        header.putLong(0L);
        Files.write(file, header.array());
    }

    @Test
    public void validateRoutedModelPaths_realGgufFile_passes() throws IOException, SrcMorphException {
        // arrange
        final Path model = modelDir.resolve("real.gguf");
        writeMinimalGguf(model);

        // act, assert -- no exception
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath(model.toString()),
                Arrays.asList(routedRule(false, "routed")));
    }

    /**
     * A file that exists but is not a model. This is the case an existence check cannot see, and it
     * is not exotic: a Git LFS pointer, an interrupted download, or a wrong path that happens to hit
     * a real file all land here. Failing at plan time matters because a multi-model run would
     * otherwise die inside the native loader <em>after</em> earlier model groups already generated.
     */
    @Test
    public void validateRoutedModelPaths_fileExistsButIsNotAGguf_throws() throws IOException {
        // arrange -- what `git clone` leaves behind without LFS installed
        final Path pointer = modelDir.resolve("real.gguf");
        Files.write(pointer, "version https://git-lfs.github.com/spec/v1\noid sha256:0\nsize 1\n".getBytes(UTF_8));
        final ModelRegistry registry = supportWithModelPath(pointer.toString());
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_LLAMACPP_JNI, registry, rules));

        // assert -- names the definition, the path, and why the file was rejected
        assertThat(thrown.getMessage(), containsString("routed"));
        assertThat(thrown.getMessage(), containsString("real.gguf"));
        assertThat(thrown.getMessage(), containsString("not a readable GGUF"));
    }

    @Test
    public void validateRoutedModelPaths_pathIsADirectoryNotAFile_throws() {
        // arrange
        final ModelRegistry registry = supportWithModelPath(modelDir.toString());
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act, assert -- a directory is not a usable model file
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_LLAMACPP_JNI, registry, rules));
    }

    @Test
    public void validateRoutedModelPaths_nullModelPath_throws() {
        // arrange
        final ModelRegistry registry = supportWithModelPath(null);
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act, assert
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_LLAMACPP_JNI, registry, rules));
    }

    /**
     * The gate that keeps every shipped example green: they all set a deliberately non-existent
     * {@code unused-with-mock-provider.gguf} and run the mock provider, which never loads it.
     *
     * @throws SrcMorphException never
     */
    @Test
    public void validateRoutedModelPaths_mockProvider_ignoresAMissingModelFile() throws SrcMorphException {
        // act, assert -- no exception despite the missing file
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_MOCK,
                supportWithModelPath("unused-with-mock-provider.gguf"),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_skipRule_isNotChecked() throws SrcMorphException {
        // act, assert -- a skip rule never loads a model, so its path is irrelevant
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Arrays.asList(routedRule(true, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_ruleWithoutDefinitionKey_isNotChecked() throws SrcMorphException {
        // act, assert -- a fallback-shaped rule with no model key routes nowhere to check
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Arrays.asList(routedRule(false, null)));
    }

    @Test
    public void validateRoutedModelPaths_nullRuleEntry_isSkipped() throws SrcMorphException {
        // act, assert -- a null entry in the list must not NPE
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Collections.<RoutingRule>singletonList(null));
    }

    // <editor-fold defaultstate="collapsed" desc="contextSize vs what the model declares">

    /** GGUF metadata value type for a UTF-8 string. */
    private static final int GGUF_TYPE_STRING = 8;

    /** GGUF metadata value type for a 32-bit unsigned integer. */
    private static final int GGUF_TYPE_UINT32 = 4;

    /** Appends a GGUF string: a 64-bit byte length followed by the UTF-8 bytes. */
    private static void putGgufString(final ByteArrayOutputStream out, final String value) throws IOException {
        final byte[] bytes = value.getBytes(UTF_8);
        final ByteBuffer length = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        length.putLong(bytes.length);
        out.write(length.array());
        out.write(bytes);
    }

    /**
     * Writes a GGUF that declares an architecture and a trained context length, which is what
     * {@code GgufMetadata.getContextLength()} needs: it resolves {@code general.architecture} first and
     * then reads {@code <arch>.context_length}. Header only — no tensors, no weights.
     *
     * @param file          where to write it
     * @param contextLength the context length to declare
     * @throws IOException if the file cannot be written
     */
    private static void writeGgufDeclaringContextLength(final Path file, final int contextLength) throws IOException {
        writeGgufDeclaringContextLength(file, contextLength, null);
    }

    /**
     * As above, optionally declaring {@code general.name} too, so the warning's "name it by the model
     * name, else by the path" choice can be exercised on both sides.
     *
     * @param file          where to write it
     * @param contextLength the context length to declare
     * @param modelName     the model name to declare, or {@code null} to declare none
     * @throws IOException if the file cannot be written
     */
    private static void writeGgufDeclaringContextLength(
            final Path file, final int contextLength, final @Nullable String modelName) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteBuffer header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        header.put(new byte[] {'G', 'G', 'U', 'F'});
        header.putInt(3);
        header.putLong(0L);
        header.putLong(modelName == null ? 2L : 3L);
        out.write(header.array());

        if (modelName != null) {
            putGgufString(out, "general.name");
            out.write(ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(GGUF_TYPE_STRING)
                    .array());
            putGgufString(out, modelName);
        }

        putGgufString(out, "general.architecture");
        out.write(ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(GGUF_TYPE_STRING)
                .array());
        putGgufString(out, "llama");

        putGgufString(out, "llama.context_length");
        out.write(ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(GGUF_TYPE_UINT32)
                .array());
        out.write(ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(contextLength)
                .array());

        Files.write(file, out.toByteArray());
    }

    private static ModelRegistry supportWithModelPathAndContextSize(final String modelPath, final int contextSize) {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setModelPath(modelPath);
        definition.setContextSize(contextSize);
        return new ModelRegistry(Collections.singletonList(definition));
    }

    private List<String> warnings() {
        final List<String> messages = new ArrayList<>();
        for (final ILoggingEvent event : logAppender.list) {
            if (event.getLevel() == Level.WARN) {
                messages.add(event.getFormattedMessage());
            }
        }
        return messages;
    }

    /**
     * The mismatch that matters. Every number the plan produces — {@code maxInputChars}, the
     * oversize/chunk decision, the time estimate — is derived from {@code contextSize}, so configuring
     * more than the model declares makes the whole plan wrong before a token is generated. A warning
     * rather than a failure, because llama.cpp will deliberately run past a model's trained context
     * with RoPE scaling.
     *
     * @throws Exception if the check fails
     */
    @Test
    public void validateRoutedModelPaths_contextSizeExceedsWhatTheModelDeclares_warns() throws Exception {
        // arrange -- a 4096-token model with the default-ish 32768 configured against it
        final Path model = modelDir.resolve("small-window.gguf");
        writeGgufDeclaringContextLength(model, 4096);

        // act
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 32768),
                Arrays.asList(routedRule(false, "routed")));

        // assert -- both numbers are in the message, so the user can act on it
        boolean warned = false;
        for (final String message : warnings()) {
            if (message.contains("32768") && message.contains("4096")) {
                warned = true;
            }
        }
        assertThat("a contextSize larger than the model's own must be reported", warned, is(true));
    }

    /**
     * A cache type the binding does not know fails the check, before the model is loaded: llama.cpp
     * would refuse it at the first file's load.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void validateRoutedModelPaths_unknownCacheType_isRefusedBeforeTheModelLoads() throws Exception {
        // arrange
        final Path model = modelDir.resolve("cache-type.gguf");
        writeGgufDeclaringContextLength(model, 4096);
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setModelPath(model.toString());
        definition.setContextSize(4096);
        definition.setCacheTypeV("q3_k");
        final ModelRegistry registry = new ModelRegistry(Collections.singletonList(definition));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                        registry,
                        Arrays.asList(routedRule(false, "routed"))));

        // assert
        assertThat(thrown.getMessage(), containsString("aiDefinition 'routed'"));
        assertThat(thrown.getMessage(), containsString("cacheTypeV"));
    }

    /**
     * When the file declares a name, the warning must use it rather than the path — that is the half of
     * the choice the nameless fixture above cannot reach, and a mutant that always picks the path would
     * otherwise survive.
     *
     * @throws Exception if the check fails
     */
    @Test
    public void validateRoutedModelPaths_mismatchOnANamedModel_namesTheModelNotThePath() throws Exception {
        // arrange
        final Path model = modelDir.resolve("named.gguf");
        writeGgufDeclaringContextLength(model, 4096, "Qwen3-0.6B");

        // act
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 32768),
                Arrays.asList(routedRule(false, "routed")));

        // assert
        assertThat(warnings(), hasItem(containsString("Qwen3-0.6B")));
    }

    /** Configuring exactly what the model declares is not a mismatch and must stay silent. */
    @Test
    public void validateRoutedModelPaths_contextSizeMatchesTheModel_isSilent() throws Exception {
        // arrange
        final Path model = modelDir.resolve("matching-window.gguf");
        writeGgufDeclaringContextLength(model, 4096);

        // act
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 4096),
                Arrays.asList(routedRule(false, "routed")));

        // assert
        assertThat(warnings(), is(Collections.<String>emptyList()));
    }

    /** A GGUF that declares no context length cannot be compared against, and must stay silent too. */
    @Test
    public void validateRoutedModelPaths_modelDeclaresNoContextLength_isSilent() throws Exception {
        // arrange
        final Path model = modelDir.resolve("no-window.gguf");
        writeMinimalGguf(model);

        // act
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 32768),
                Arrays.asList(routedRule(false, "routed")));

        // assert
        assertThat(warnings(), is(Collections.<String>emptyList()));
    }

    // </editor-fold>

    @Test
    public void validateRoutedModelPaths_remoteModelInAJniRun_needsNoModelFile() throws SrcMorphException {
        // act, assert -- no exception
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithRemote(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, "https://openrouter.ai/api/v1"),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_remoteModel_acceptsHttpAndATrimmedUrl() throws SrcMorphException {
        // act, assert -- no exception
        EngineSupport.validateRoutedModelPaths(
                GenerationProviderFactory.PROVIDER_MOCK,
                supportWithRemote(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, " http://127.0.0.1:8080/v1 "),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_remoteModelWithoutAnHttpUrl_throwsNamingTheDefinition() {
        // arrange
        final ModelRegistry registry =
                supportWithRemote(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, "ftp://example.org");
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(GenerationProviderFactory.PROVIDER_MOCK, registry, rules));

        // assert
        assertThat(thrown.getMessage(), containsString("routed"));
        assertThat(thrown.getMessage(), containsString("ftp://example.org"));
    }

    @Test
    public void validateRoutedModelPaths_remoteModelWithAnUnsetApiKeyEnv_throwsBeforeAnyRequest() {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setProvider(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
        definition.setBaseUrl("https://openrouter.ai/api/v1");
        definition.setApiKeyEnv("SRCMORPH_TEST_KEY_THAT_IS_NEVER_SET");
        final ModelRegistry remote = new ModelRegistry(Arrays.asList(definition));
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(GenerationProviderFactory.PROVIDER_MOCK, remote, rules));

        // assert
        assertThat(
                thrown.getMessage(),
                is("aiDefinition 'routed': its apiKeyEnv names SRCMORPH_TEST_KEY_THAT_IS_NEVER_SET, which is not set"
                        + " in this process's environment (checked before any request is sent)"));
    }

    @Test
    public void validateRoutedModelPaths_runProviderOpenAiCompatible_checksTheUrlToo() {
        // arrange
        final ModelRegistry registry = supportWithRemote("", "not a url");
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act, assert
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, registry, rules));
    }

    @Test
    public void validateRoutedModelPaths_jniModelInAMockRun_isChecked() {
        // arrange
        // The gate is per model: a definition that names llamacpp-jni itself is checked in a mock run.
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("routed");
        definition.setProvider(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        definition.setModelPath(modelDir.resolve("nope.gguf").toString());
        final ModelRegistry registry = new ModelRegistry(Arrays.asList(definition));
        final List<RoutingRule> rules = Arrays.asList(routedRule(false, "routed"));

        // act, assert
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(GenerationProviderFactory.PROVIDER_MOCK, registry, rules));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="resolveFactsKeys">
    @Test
    public void resolveFactsKeys_aPhaseRuleWithAnInvalidInlinePattern_isRefusedBeforeAModelLoads() {
        // arrange: a package rule names an inline definition whose pattern does not compile
        final FactCounter counter = new FactCounter();
        counter.setLabel("broken");
        counter.setPattern("(");
        final FactDefinition definition = new FactDefinition();
        definition.setKey("pk");
        definition.setFacts(Collections.singletonList(counter));
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setFactDefinitions(Collections.singletonList(definition));
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("package-body");
        rule.setAiDefinitionKey("default");
        rule.setFactsKey("pk");
        final List<RoutingRule> rules = Collections.singletonList(rule);

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> EngineSupport.resolveFactsKeys(config, rules));

        // assert: a rule without an id is named by its prompt key
        assertThat(thrown.getMessage(), containsString("broken"));
        assertThat(thrown.getMessage(), containsString("the facts of rule 'package-body'"));
    }

    @Test
    public void resolveFactsKeys_aRuleWithAnIdAndAnInvalidInlinePattern_isNamedByItsId() {
        // arrange
        final FactCounter counter = new FactCounter();
        counter.setLabel("broken");
        counter.setPattern("(");
        final FactDefinition definition = new FactDefinition();
        definition.setKey("pk");
        definition.setFacts(Collections.singletonList(counter));
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setFactDefinitions(Collections.singletonList(definition));
        final RoutingRule rule = new RoutingRule();
        rule.setId("packages");
        rule.setPromptKey("package-body");
        rule.setAiDefinitionKey("default");
        rule.setFactsKey("pk");
        final List<RoutingRule> rules = Collections.singletonList(rule);

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> EngineSupport.resolveFactsKeys(config, rules));

        // assert
        assertThat(thrown.getMessage(), containsString("the facts of rule 'packages'"));
    }
    // </editor-fold>
}
