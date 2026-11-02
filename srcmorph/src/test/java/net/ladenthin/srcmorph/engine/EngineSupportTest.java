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
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfig;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

public class EngineSupportTest {

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    public void attachLogAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(EngineSupport.class);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(logAppender);
    }

    @AfterEach
    public void detachLogAppender() {
        ((Logger) LoggerFactory.getLogger(EngineSupport.class)).detachAppender(logAppender);
        logAppender.stop();
    }

    @TempDir
    Path tempDir;

    // <editor-fold defaultstate="collapsed" desc="resolveSubtrees">
    @Test
    public void resolveSubtrees_nullReturnsEmpty() throws SrcMorphException {
        assertThat(EngineSupport.resolveSubtrees(tempDir, null), is(Collections.<Path>emptyList()));
    }

    @Test
    public void resolveSubtrees_emptyReturnsEmpty() throws SrcMorphException {
        assertThat(
                EngineSupport.resolveSubtrees(tempDir, Collections.<String>emptyList()),
                is(Collections.<Path>emptyList()));
    }

    @Test
    public void resolveSubtrees_earlyReturnResultIsMutable() throws SrcMorphException {
        // The early-return (null/empty subtrees) branch returns the same mutable ArrayList as the
        // loop-completion branch, not an immutable Collections.emptyList(), so callers can treat every
        // result uniformly. Mutating it here must not throw UnsupportedOperationException.
        final List<Path> resolved = EngineSupport.resolveSubtrees(tempDir, null);
        resolved.add(tempDir);
        assertThat(resolved, hasItem(tempDir));
    }

    @Test
    public void resolveSubtrees_existingSubtreeIsResolved() throws IOException, SrcMorphException {
        final Path sub = tempDir.resolve("exists");
        Files.createDirectories(sub);

        final List<Path> resolved = EngineSupport.resolveSubtrees(tempDir, Arrays.asList("exists"));

        assertThat(resolved, hasItem(sub.normalize()));
    }

    @Test
    public void resolveSubtrees_missingSubtree_isRefused() {
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class, () -> EngineSupport.resolveSubtrees(tempDir, Arrays.asList("does-not-exist")));

        assertThat(thrown.getMessage(), containsString("Subtree 'does-not-exist' does not exist: "));
    }

    @Test
    public void resolveSubtrees_aFileInsteadOfADirectory_isRefused() throws IOException {
        Files.write(tempDir.resolve("plain.txt"), new byte[0]);

        assertThrows(SrcMorphException.class, () -> EngineSupport.resolveSubtrees(tempDir, Arrays.asList("plain.txt")));
    }

    @Test
    public void resolveSubtrees_outsideTheBaseDirectory_isRefused() throws IOException {
        final Path base = Files.createDirectories(tempDir.resolve("base"));
        Files.createDirectories(tempDir.resolve("other"));

        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class, () -> EngineSupport.resolveSubtrees(base, Arrays.asList("../other")));

        assertThat(thrown.getMessage(), containsString("outside the base directory"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildPromptSupport">
    @Test
    public void buildPromptSupport_nullListBuildsEmptySupport() throws SrcMorphException {
        final AiPromptSupport support = EngineSupport.buildPromptSupport(null, null, null);
        assertThat(support, is(notNullValue()));
    }

    @Test
    public void buildPromptSupport_missingKeyThrowsSrcMorphException() {
        final AiPromptDefinition bad = new AiPromptDefinition();
        bad.setTemplate("template with no key");

        final SrcMorphException e = assertThrows(
                SrcMorphException.class, () -> EngineSupport.buildPromptSupport(Arrays.asList(bad), null, null));
        assertThat(e.getMessage(), containsString("Invalid plugin configuration:"));
    }

    @Test
    public void buildPromptSupport_templateFile_isReadFromTheProfileDirectory() throws Exception {
        Files.write(tempDir.resolve("file-body-java.md"), "You are a Java code indexer.".getBytes(UTF_8));
        final AiPromptDefinition fromFile = new AiPromptDefinition();
        fromFile.setKey("file-body-java");
        fromFile.setTemplateFile("file-body-java.md");

        final AiPromptSupport support =
                EngineSupport.buildPromptSupport(Arrays.asList(fromFile), tempDir.toFile(), null);

        assertThat(support.systemPrompt("file-body-java"), is("You are a Java code indexer."));
    }

    @Test
    public void buildPromptSupport_aRulesMapAndReducePrompts_areReadFromTheProfileDirectory() throws Exception {
        Files.write(tempDir.resolve("file-body-java.md"), "BODY".getBytes(UTF_8));
        Files.write(tempDir.resolve("map-chunk.md"), "MAP".getBytes(UTF_8));
        Files.write(tempDir.resolve("reduce-parts.md"), "REDUCE".getBytes(UTF_8));
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey("file-body-java");
        rule.setMapPromptKey("map-chunk");
        rule.setReducePromptKey("reduce-parts");
        final AiFieldGenerationConfig plain = new AiFieldGenerationConfig();
        plain.setPromptKey("file-body-java");

        final AiPromptSupport support =
                EngineSupport.buildPromptSupport(null, tempDir.toFile(), Arrays.asList(rule, plain));

        assertThat(support.systemPrompt("map-chunk"), is("MAP"));
        assertThat(support.systemPrompt("reduce-parts"), is("REDUCE"));
        assertThat(support.systemPrompt("file-body-java+map-chunk"), is("BODY\n\nMAP"));
    }

    @Test
    public void buildPromptSupport_missingTemplateFile_throwsSrcMorphException() {
        final AiPromptDefinition fromFile = new AiPromptDefinition();
        fromFile.setKey("file-body-java");
        fromFile.setTemplateFile("missing.md");

        final SrcMorphException e = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.buildPromptSupport(Arrays.asList(fromFile), tempDir.toFile(), null));

        assertThat(e.getMessage(), containsString("Invalid plugin configuration: promptDefinitions[0]"));
        assertThat(e.getMessage(), containsString("missing.md cannot be read"));
    }

    @Test
    public void buildPromptSupport_templateAndTemplateFile_throwsSrcMorphException() {
        final AiPromptDefinition both = new AiPromptDefinition();
        both.setKey("file-body-java");
        both.setTemplate("inline");
        both.setTemplateFile("file.md");

        final SrcMorphException e = assertThrows(
                SrcMorphException.class, () -> EngineSupport.buildPromptSupport(Arrays.asList(both), null, null));

        assertThat(e.getMessage(), containsString("sets both template and templateFile"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildAiModelDefinitionSupport">
    @Test
    public void buildAiModelDefinitionSupport_nullListBuildsEmptySupport() throws SrcMorphException {
        assertThat(EngineSupport.buildAiModelDefinitionSupport(null, null), is(notNullValue()));
    }

    @Test
    public void buildAiModelDefinitionSupport_resolvesAgainstTheModelDirectory() throws SrcMorphException {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("named");
        definition.setModelPath("named.gguf");
        final File modelDirectory = new File("models");

        final AiModelDefinitionSupport support =
                EngineSupport.buildAiModelDefinitionSupport(Arrays.asList(definition), modelDirectory);

        assertThat(support.getConfig("named").getModelPath(), is(new File(modelDirectory, "named.gguf").getPath()));
    }

    @Test
    public void buildAiModelDefinitionSupport_missingKeyThrowsSrcMorphException() {
        final AiModelDefinition bad = new AiModelDefinition();
        bad.setModelPath("model.gguf");

        final SrcMorphException e = assertThrows(
                SrcMorphException.class, () -> EngineSupport.buildAiModelDefinitionSupport(Arrays.asList(bad), null));
        assertThat(e.getMessage(), containsString("Invalid plugin configuration:"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requireDefinedModels">
    @Test
    public void requireDefinedModels_anUndefinedModel_namesTheRuleAndTheKey() {
        final AiFieldGenerationConfig rule = phaseRule("file-body-java", "no-such-model");
        rule.setId("java");
        final AiFieldGenerationConfig unnamed = phaseRule("file-body-sql", "other-missing");

        final SrcMorphException byId = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.requireDefinedModels(new AiModelDefinitionSupport(null), Arrays.asList(rule)));
        final SrcMorphException byPrompt = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.requireDefinedModels(new AiModelDefinitionSupport(null), Arrays.asList(unnamed)));

        assertThat(
                byId.getMessage(),
                containsString("The rule 'java' names the model 'no-such-model', which no aiDefinition defines"));
        assertThat(byPrompt.getMessage(), containsString("The rule 'file-body-sql' names the model 'other-missing'"));
    }

    @Test
    public void requireDefinedModels_skipRulesAndRulesWithoutAModel_needNone() throws SrcMorphException {
        final AiFieldGenerationConfig skip = phaseRule("p", "no-such-model");
        skip.setSkip(true);
        final AiFieldGenerationConfig noModel = new AiFieldGenerationConfig();

        EngineSupport.requireDefinedModels(new AiModelDefinitionSupport(null), Arrays.asList(skip, noModel, null));
    }

    @Test
    public void requireDefinedModels_aDefinedModel_passes() throws SrcMorphException {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("m");
        definition.setModelPath("m.gguf");

        EngineSupport.requireDefinedModels(
                new AiModelDefinitionSupport(Arrays.asList(definition)), Arrays.asList(phaseRule("p", "m")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="phaseRule">
    private static AiFieldGenerationConfig phaseRule(final String promptKey, final String aiDefinitionKey) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(promptKey);
        rule.setAiDefinitionKey(aiDefinitionKey);
        return rule;
    }

    @Test
    public void requiredPhaseRule_exactlyOne_isReturned() throws SrcMorphException {
        final AiFieldGenerationConfig rule = phaseRule("package-body", "m");

        final AiFieldGenerationConfig returned = EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule));

        assertThat(returned.getPromptKey(), is("package-body"));
        assertThat(returned.getAiDefinitionKey(), is("m"));
    }

    @Test
    public void phaseRule_withoutOnOversize_readsAllOfItsInput_theConfigurationStaysAsGiven() throws SrcMorphException {
        // arrange
        final AiFieldGenerationConfig rule = phaseRule("package-body", "m");

        // act
        final AiFieldGenerationConfig required = EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule));
        final AiFieldGenerationConfig optional = EngineSupport.optionalPhaseRule("p", "k", Arrays.asList(rule));

        // assert
        assertThat(required.oversizeStrategy(), is(net.ladenthin.srcmorph.config.AiOversizeStrategy.MAP_REDUCE));
        assertThat(optional.oversizeStrategy(), is(net.ladenthin.srcmorph.config.AiOversizeStrategy.MAP_REDUCE));
        assertThat(rule.getOnOversize(), is(nullValue()));
    }

    @Test
    public void phaseRule_withAnOnOversize_keepsIt() throws SrcMorphException {
        final AiFieldGenerationConfig rule = phaseRule("package-body", "m");
        rule.setOnOversize("fail");

        assertThat(
                EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule)).oversizeStrategy(),
                is(net.ladenthin.srcmorph.config.AiOversizeStrategy.FAIL));
    }

    @Test
    public void requiredPhaseRule_noneOrTwo_isRefusedNamingThePhaseAndTheCliKey() {
        for (final List<AiFieldGenerationConfig> rules : Arrays.asList(
                null,
                Collections.<AiFieldGenerationConfig>emptyList(),
                Arrays.asList(phaseRule("a", "m"), phaseRule("b", "m")))) {
            final SrcMorphException thrown = assertThrows(
                    SrcMorphException.class,
                    () -> EngineSupport.requiredPhaseRule("aggregate-packages", "packageFieldGenerations", rules));

            assertThat(thrown.getMessage(), containsString("aggregate-packages takes exactly one rule"));
            assertThat(thrown.getMessage(), containsString("packageFieldGenerations in the CLI"));
            assertThat(thrown.getMessage(), containsString((rules == null ? 0 : rules.size()) + " given"));
        }
    }

    @Test
    public void requiredPhaseRule_withoutPromptOrModel_isRefused() {
        for (final AiFieldGenerationConfig rule :
                Arrays.asList(phaseRule("", "m"), phaseRule("p", " "), new AiFieldGenerationConfig())) {
            final SrcMorphException thrown = assertThrows(
                    SrcMorphException.class, () -> EngineSupport.requiredPhaseRule("p", "k", Arrays.asList(rule)));

            assertThat(thrown.getMessage(), containsString("needs a promptKey and an aiDefinitionKey"));
        }
    }

    @Test
    public void optionalPhaseRule_none_isNull() throws SrcMorphException {
        assertThat(EngineSupport.optionalPhaseRule("p", "k", null), is(nullValue()));
        assertThat(
                EngineSupport.optionalPhaseRule("p", "k", Collections.<AiFieldGenerationConfig>emptyList()),
                is(nullValue()));
    }

    @Test
    public void optionalPhaseRule_one_isReturned_two_areRefused() throws SrcMorphException {
        final AiFieldGenerationConfig rule = phaseRule("project-body", "m");
        assertThat(
                EngineSupport.optionalPhaseRule("p", "k", Arrays.asList(rule)).getPromptKey(), is("project-body"));

        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.optionalPhaseRule(
                        "aggregate-project", "projectFieldGenerations", Arrays.asList(rule, rule)));
        assertThat(thrown.getMessage(), containsString("aggregate-project takes at most one rule"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="resolveLlamaCppJniConfig">
    @Test
    public void resolveLlamaCppJniConfig_byKey_looksUpNamedDefinition() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();

        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("named");
        definition.setModelPath("named.gguf");
        final AiModelDefinitionSupport modelDefinitionSupport = new AiModelDefinitionSupport(Arrays.asList(definition));

        final LlamaCppJniConfig result = EngineSupport.resolveLlamaCppJniConfig(modelDefinitionSupport, "named");

        assertThat(result.modelPath(), is("named.gguf"));
    }

    @Test
    public void resolveLlamaCppJniConfig_byKey_missingKeyThrowsIllegalArgumentException() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        final AiModelDefinitionSupport modelDefinitionSupport = new AiModelDefinitionSupport(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> EngineSupport.resolveLlamaCppJniConfig(modelDefinitionSupport, "missing"));
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
    private static AiModelDefinitionSupport supportWithModelPath(final String modelPath) {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("routed");
        definition.setModelPath(modelPath);
        return new AiModelDefinitionSupport(Arrays.asList(definition));
    }

    /**
     * Builds a lookup holding one remote definition.
     *
     * @param provider the definition's own provider
     * @param baseUrl  its base URL
     * @return the lookup
     */
    private static AiModelDefinitionSupport supportWithRemote(final String provider, final String baseUrl) {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("routed");
        definition.setProvider(provider);
        definition.setBaseUrl(baseUrl);
        return new AiModelDefinitionSupport(Arrays.asList(definition));
    }

    /**
     * Builds a routing rule pointing at the {@code routed} definition.
     *
     * @param skip           whether the rule is a skip rule
     * @param aiDefinitionKey the definition key, may be {@code null}
     * @return the rule
     */
    private static AiFieldGenerationConfig routedRule(final boolean skip, final String aiDefinitionKey) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey("p1");
        rule.setAiDefinitionKey(aiDefinitionKey);
        rule.setSkip(skip);
        return rule;
    }

    @Test
    public void validateRoutedModelPaths_anUnknownProvider_isRefusedBeforeTheRun() {
        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        "mock",
                        supportWithRemote("openai-compatibel", "http://127.0.0.1:1/v1"),
                        Arrays.asList(routedRule(false, "routed"))));

        // assert
        assertThat(thrown.getMessage(), containsString("unknown provider 'openai-compatibel'"));
    }

    @Test
    public void validateRoutedModelPaths_theMockProvider_passes() throws SrcMorphException {
        EngineSupport.validateRoutedModelPaths(
                "mock", supportWithModelPath("absent.gguf"), Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_missingModelFile_throwsNamingTheDefinitionAndPath() {
        // arrange
        final String missing = modelDir.resolve("nope.gguf").toString();

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                        supportWithModelPath(missing),
                        Arrays.asList(routedRule(false, "routed"))));

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

        // act / assert -- no exception
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
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
        Files.write(
                pointer,
                "version https://git-lfs.github.com/spec/v1\noid sha256:0\nsize 1\n"
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                        supportWithModelPath(pointer.toString()),
                        Arrays.asList(routedRule(false, "routed"))));

        // assert -- names the definition, the path, and why the file was rejected
        assertThat(thrown.getMessage(), containsString("routed"));
        assertThat(thrown.getMessage(), containsString("real.gguf"));
        assertThat(thrown.getMessage(), containsString("not a readable GGUF"));
    }

    @Test
    public void validateRoutedModelPaths_pathIsADirectoryNotAFile_throws() {
        // act / assert -- a directory is not a usable model file
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                        supportWithModelPath(modelDir.toString()),
                        Arrays.asList(routedRule(false, "routed"))));
    }

    @Test
    public void validateRoutedModelPaths_nullModelPath_throws() {
        // act / assert
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                        supportWithModelPath(null),
                        Arrays.asList(routedRule(false, "routed"))));
    }

    /**
     * The gate that keeps every shipped example green: they all set a deliberately non-existent
     * {@code unused-with-mock-provider.gguf} and run the mock provider, which never loads it.
     *
     * @throws SrcMorphException never
     */
    @Test
    public void validateRoutedModelPaths_mockProvider_ignoresAMissingModelFile() throws SrcMorphException {
        // act / assert -- no exception despite the missing file
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_MOCK,
                supportWithModelPath("unused-with-mock-provider.gguf"),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_skipRule_isNotChecked() throws SrcMorphException {
        // act / assert -- a skip rule never loads a model, so its path is irrelevant
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Arrays.asList(routedRule(true, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_ruleWithoutDefinitionKey_isNotChecked() throws SrcMorphException {
        // act / assert -- a fallback-shaped rule with no model key routes nowhere to check
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Arrays.asList(routedRule(false, null)));
    }

    @Test
    public void validateRoutedModelPaths_nullRuleEntry_isSkipped() throws SrcMorphException {
        // act / assert -- a null entry in the list must not NPE
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPath("missing.gguf"),
                Collections.<AiFieldGenerationConfig>singletonList(null));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="contextSize vs what the model declares">

    /** GGUF metadata value type for a UTF-8 string. */
    private static final int GGUF_TYPE_STRING = 8;

    /** GGUF metadata value type for a 32-bit unsigned integer. */
    private static final int GGUF_TYPE_UINT32 = 4;

    /** Appends a GGUF string: a 64-bit byte length followed by the UTF-8 bytes. */
    private static void putGgufString(final ByteArrayOutputStream out, final String value) throws IOException {
        final byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
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

    private static AiModelDefinitionSupport supportWithModelPathAndContextSize(
            final String modelPath, final int contextSize) {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("routed");
        definition.setModelPath(modelPath);
        definition.setContextSize(contextSize);
        return new AiModelDefinitionSupport(Collections.singletonList(definition));
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
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
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
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 32768),
                Arrays.asList(routedRule(false, "routed")));

        // assert
        assertThat(warnings(), hasItem(containsString("Qwen3-0.6B")));
    }

    /** Configuring exactly what the model declares is not a mismatch and must stay silent. */
    @Test
    public void validateRoutedModelPaths_contextSizeMatchesTheModel_isSilent() throws Exception {
        final Path model = modelDir.resolve("matching-window.gguf");
        writeGgufDeclaringContextLength(model, 4096);

        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 4096),
                Arrays.asList(routedRule(false, "routed")));

        assertThat(warnings(), is(Collections.<String>emptyList()));
    }

    /** A GGUF that declares no context length cannot be compared against, and must stay silent too. */
    @Test
    public void validateRoutedModelPaths_modelDeclaresNoContextLength_isSilent() throws Exception {
        final Path model = modelDir.resolve("no-window.gguf");
        writeMinimalGguf(model);

        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithModelPathAndContextSize(model.toString(), 32768),
                Arrays.asList(routedRule(false, "routed")));

        assertThat(warnings(), is(Collections.<String>emptyList()));
    }

    // </editor-fold>

    @Test
    public void validateRoutedModelPaths_remoteModelInAJniRun_needsNoModelFile() throws SrcMorphException {
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI,
                supportWithRemote(
                        AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, "https://openrouter.ai/api/v1"),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_remoteModel_acceptsHttpAndATrimmedUrl() throws SrcMorphException {
        EngineSupport.validateRoutedModelPaths(
                AiGenerationProviderFactory.PROVIDER_MOCK,
                supportWithRemote(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, " http://127.0.0.1:8080/v1 "),
                Arrays.asList(routedRule(false, "routed")));
    }

    @Test
    public void validateRoutedModelPaths_remoteModelWithoutAnHttpUrl_throwsNamingTheDefinition() {
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_MOCK,
                        supportWithRemote(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, "ftp://example.org"),
                        Arrays.asList(routedRule(false, "routed"))));
        assertThat(thrown.getMessage(), containsString("routed"));
        assertThat(thrown.getMessage(), containsString("ftp://example.org"));
    }

    @Test
    public void validateRoutedModelPaths_remoteModelWithAnUnsetApiKeyEnv_throwsBeforeAnyRequest() {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("routed");
        definition.setProvider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
        definition.setBaseUrl("https://openrouter.ai/api/v1");
        definition.setApiKeyEnv("SRCMORPH_TEST_KEY_THAT_IS_NEVER_SET");
        final AiModelDefinitionSupport remote = new AiModelDefinitionSupport(Arrays.asList(definition));

        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_MOCK, remote, Arrays.asList(routedRule(false, "routed"))));

        assertThat(
                thrown.getMessage(),
                is("aiDefinition 'routed': its apiKeyEnv names SRCMORPH_TEST_KEY_THAT_IS_NEVER_SET, which is not set"
                        + " in this process's environment (checked before any request is sent)"));
    }

    @Test
    public void validateRoutedModelPaths_runProviderOpenAiCompatible_checksTheUrlToo() {
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE,
                        supportWithRemote("", "not a url"),
                        Arrays.asList(routedRule(false, "routed"))));
    }

    @Test
    public void validateRoutedModelPaths_jniModelInAMockRun_isChecked() {
        // The gate is per model: a definition that names llamacpp-jni itself is checked in a mock run.
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("routed");
        definition.setProvider(AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        definition.setModelPath(modelDir.resolve("nope.gguf").toString());
        assertThrows(
                SrcMorphException.class,
                () -> EngineSupport.validateRoutedModelPaths(
                        AiGenerationProviderFactory.PROVIDER_MOCK,
                        new AiModelDefinitionSupport(Arrays.asList(definition)),
                        Arrays.asList(routedRule(false, "routed"))));
    }
}
