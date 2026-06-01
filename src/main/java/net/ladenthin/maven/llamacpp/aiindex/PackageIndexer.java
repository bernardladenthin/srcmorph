// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.plugin.logging.Log;
import org.jspecify.annotations.Nullable;

/**
 * Aggregates {@code package.ai.md} index files: walks the output tree, lists each
 * package's contents, and fills in AI-generated summary and keyword fields.
 */
public class PackageIndexer {

    /**
     * Section heading inserted above the contents listing in both the package source text
     * and the default package body.
     */
    private static final String CONTENTS_HEADING = "#### Contents";

    /**
     * Earliest possible date value used as the starting point when scanning child nodes
     * to find the latest index creation date.
     */
    private static final String EPOCH_DATE = "1970-01-01T00:00:00Z";

    /**
     * Context-type label passed to {@link AiFieldGenerationSupport} so that trim-warning
     * log messages read "Trimmed AI input for package field '…'".
     */
    private static final String CONTEXT_TYPE_PACKAGE = "package";

    /**
     * Comparator that orders {@link Path} instances by their file name component only,
     * producing a consistent, platform-independent sort order when listing directory entries.
     */
    private static final Comparator<Path> BY_FILE_NAME =
            Comparator.comparing(p -> {
                final Path fileName = p.getFileName();
                // Files.list never returns the filesystem root, so getFileName() is non-null here.
                if (fileName == null) {
                    throw new IllegalStateException("Path has no file-name component: " + p);
                }
                return fileName.toString();
            });

    private final Log log;
    private final Path outputRoot;
    private final String pluginVersion;
    private final String aiVersion;
    private final List<Path> sourceSubtrees;
    private final List<Path> aiSubtrees;
    private final boolean force;

    private final @Nullable List<AiFieldGenerationConfig> fieldGenerations;

    private final AiPathSupport pathSupport = new AiPathSupport();
    private final AiTimeSupport timeSupport = new AiTimeSupport();
    private final AiChecksumSupport checksumSupport = new AiChecksumSupport();
    private final AiMdHeaderSupport headerSupport = new AiMdHeaderSupport();
    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    private final AiFieldGenerationSupport fieldGenerationSupport;

    /**
     * Creates a new {@link PackageIndexer}.
     *
     * @param log                    Maven plugin logger
     * @param baseDirectory          project base directory
     * @param outputRoot             root directory in which {@code .ai.md} files reside
     * @param pluginVersion          plugin version recorded in headers
     * @param aiVersion              AI summarisation logic version recorded in headers
     * @param sourceSubtrees         source subtrees in scope; may be empty
     * @param force                  when {@code true}, regenerate even when fields are populated
     * @param generationProvider     AI provider used to generate fields
     * @param fieldGenerations       field generation configurations; may be {@code null}
     * @param promptSupport          prompt lookup
     * @param modelDefinitionSupport AI model definition lookup
     */
    public PackageIndexer(
            final Log log,
            final Path baseDirectory,
            final Path outputRoot,
            final String pluginVersion,
            final String aiVersion,
            final Collection<Path> sourceSubtrees,
            final boolean force,
            final AiGenerationProvider generationProvider,
            final @Nullable Collection<AiFieldGenerationConfig> fieldGenerations,
            final AiPromptSupport promptSupport,
            final AiModelDefinitionSupport modelDefinitionSupport) {
        this.log = log;
        this.outputRoot = outputRoot;
        this.pluginVersion = pluginVersion;
        this.aiVersion = aiVersion;
        this.sourceSubtrees = new ArrayList<>(sourceSubtrees);
        // Inlined here (instead of calling toAiSubtrees) so the Checker Framework
        // does not flag a method invocation on an @UnderInitialization receiver.
        final List<Path> ai = new ArrayList<>(this.sourceSubtrees.size());
        for (Path sourceSubtree : this.sourceSubtrees) {
            final Path relative = pathSupport.relativizeFromSrc(baseDirectory, sourceSubtree);
            ai.add(outputRoot.resolve(relative).normalize());
        }
        this.aiSubtrees = ai;
        this.force = force;
        this.fieldGenerations = fieldGenerations != null ? new ArrayList<>(fieldGenerations) : null;
        this.fieldGenerationSupport = new AiFieldGenerationSupport(
                log, generationProvider, new AiPromptPreparationSupport(promptSupport), modelDefinitionSupport);
    }

    /**
     * Aggregates all package directories beneath {@code rootDirectory}.
     *
     * @param rootDirectory output root directory to walk
     * @return number of package index files written or refreshed
     * @throws IOException if the output tree cannot be read or written
     */
    public int aggregate(final Path rootDirectory) throws IOException {
        return aggregateRecursive(rootDirectory);
    }

    private int aggregateRecursive(final Path directory) throws IOException {
        int count = 0;

        final List<Path> subDirectories;
        try (Stream<Path> stream = Files.list(directory)) {
            subDirectories =
                    compatibilityHelper.toList(stream.filter(Files::isDirectory).sorted());
        }

        for (Path subDirectory : subDirectories) {
            count += aggregateRecursive(subDirectory);
        }

        if (!matchesAggregationScope(directory)) {
            return count;
        }

        if (shouldCreatePackageFile(directory)) {
            writePackageFile(directory);
            count++;
        }

        return count;
    }

    private boolean matchesAggregationScope(final Path directory) {
        if (aiSubtrees.isEmpty()) {
            return true;
        }

        for (Path aiSubtree : aiSubtrees) {
            if (directory.startsWith(aiSubtree)) {
                return true;
            }
            if (aiSubtree.startsWith(directory)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldCreatePackageFile(final Path directory) throws IOException {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream.anyMatch(path -> {
                final Path fileName = path.getFileName();
                // Files.list never returns the filesystem root, so getFileName() is non-null here.
                if (fileName == null) {
                    return false;
                }
                final String name = fileName.toString();
                if (Files.isDirectory(path)) {
                    return hasPackageAiMdFile(path);
                }
                return isAiMdContentFile(name);
            });
        }
    }

    /**
     * Returns {@code true} when {@code directory} contains a
     * {@link AiMdHeaderCodec#PACKAGE_AI_MD_FILENAME} file.
     *
     * @param directory the directory to examine
     * @return {@code true} if the package AI index file exists inside {@code directory}
     */
    private boolean hasPackageAiMdFile(final Path directory) {
        return Files.exists(directory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME));
    }

    private void writePackageFile(final Path directory) throws IOException {
        if (fieldGenerations == null || fieldGenerations.isEmpty()) {
            throw new IllegalArgumentException("No field generations configured for package indexing.");
        }

        final List<String> contents = collectContents(directory);
        final Path packageFile = directory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME);

        final String nodeName = outputRoot.relativize(directory).toString().replace('\\', '/');
        final String title = nodeName.isEmpty() ? "ai" : nodeName;
        final String packageChecksum = calculatePackageChecksum(directory);
        final String packageDate = calculatePackageDate(directory);
        final String generatedAt = timeSupport.formatInstant(java.time.Instant.now());

        final AiMdHeader baseHeader = new AiMdHeader(
                title,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                packageChecksum,
                packageDate,
                generatedAt,
                pluginVersion,
                aiVersion,
                AiMdHeaderCodec.NODE_TYPE_PACKAGE);

        if (!headerSupport.shouldWrite(force, packageFile, baseHeader)) {
            log.info("Unchanged package AI index file: " + packageFile);
            return;
        }

        final String sourceText = buildPackageSourceText(baseHeader, contents);

        final AiGenerationResult result = fieldGenerationSupport.processFieldGenerations(
                fieldGenerations, packageFile, CONTEXT_TYPE_PACKAGE, sourceText, baseHeader);

        final String body = (result.body() == null || compatibilityHelper.isBlank(result.body()))
                ? buildDefaultPackageBody(contents)
                : result.body();

        final AiMdDocument document = new AiMdDocument(baseHeader, body);
        documentCodec.write(packageFile, document);

        log.info("Wrote package AI index file: " + packageFile);
    }

    private List<String> collectContents(final Path directory) throws IOException {
        final List<String> entries = new ArrayList<>();

        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : compatibilityHelper.toList(stream.sorted(BY_FILE_NAME))) {
                final Path fileNamePath = path.getFileName();
                if (fileNamePath == null) {
                    continue;
                }
                final String name = fileNamePath.toString();

                if (Files.isDirectory(path)) {
                    if (hasPackageAiMdFile(path)) {
                        entries.add(name + "/");
                    }
                    continue;
                }

                if (isAiMdContentFile(name)) {
                    entries.add(name);
                }
            }
        }

        return entries;
    }

    private String buildPackageSourceText(final AiMdHeader header, final Collection<String> contents) {
        final StringBuilder builder = new StringBuilder();
        builder.append(AiMdHeaderCodec.HEADER_TITLE_PREFIX)
                .append(header.title())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("H: ")
                .append(header.h())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("C: ")
                .append(header.c())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("D: ")
                .append(header.d())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("T: ")
                .append(header.t())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("G: ")
                .append(header.g())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("A: ")
                .append(header.a())
                .append('\n');
        builder.append(AiMdHeaderCodec.HEADER_FIELD_PREFIX)
                .append("X: ")
                .append(header.x())
                .append('\n');
        appendContentsSection(builder, contents, true);
        return builder.toString();
    }

    private String buildDefaultPackageBody(final Collection<String> contents) {
        final StringBuilder builder = new StringBuilder();
        appendContentsSection(builder, contents, false);
        return builder.toString();
    }

    /**
     * Appends the {@link #CONTENTS_HEADING} and the content entry list to {@code builder}.
     * Does nothing when {@code contents} is empty.
     *
     * @param builder        target string builder
     * @param contents       collection of content entry names
     * @param prependNewline when {@code true}, a blank line is prepended before the heading
     */
    private void appendContentsSection(
            final StringBuilder builder, final Collection<String> contents, final boolean prependNewline) {
        if (contents.isEmpty()) {
            return;
        }
        if (prependNewline) {
            builder.append('\n');
        }
        builder.append(CONTENTS_HEADING).append('\n');
        for (String entry : contents) {
            builder.append("- ").append(entry).append('\n');
        }
    }

    private String calculatePackageChecksum(final Path directory) throws IOException {
        final StringBuilder builder = new StringBuilder();

        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : compatibilityHelper.toList(stream.sorted(BY_FILE_NAME))) {
                final Path fileNamePath = path.getFileName();
                if (fileNamePath == null) {
                    continue;
                }
                final String name = fileNamePath.toString();

                if (Files.isDirectory(path)) {
                    if (hasPackageAiMdFile(path)) {
                        builder.append(headerSupport.buildChecksumLine(name, readChildPackageHeader(path)));
                    }
                    continue;
                }

                if (isAiMdContentFile(name)) {
                    builder.append(headerSupport.buildChecksumLine(
                            name, documentCodec.read(path).header()));
                }
            }
        }

        return checksumSupport.calculateCrc32Hex(builder.toString());
    }

    private String calculatePackageDate(final Path directory) throws IOException {
        String latest = EPOCH_DATE;

        try (Stream<Path> stream = Files.list(directory)) {
            for (Path path : compatibilityHelper.toList(stream.sorted(BY_FILE_NAME))) {
                final Path fileNamePath = path.getFileName();
                if (fileNamePath == null) {
                    continue;
                }
                final String name = fileNamePath.toString();

                if (Files.isDirectory(path)) {
                    if (hasPackageAiMdFile(path)) {
                        latest = laterDate(latest, readChildPackageHeader(path).d());
                    }
                    continue;
                }

                if (isAiMdContentFile(name)) {
                    latest = laterDate(latest, documentCodec.read(path).header().d());
                }
            }
        }

        return latest;
    }

    /**
     * Reads and returns the {@link AiMdHeader} from the
     * {@link AiMdHeaderCodec#PACKAGE_AI_MD_FILENAME} file inside {@code directory}.
     * The caller must ensure {@link #hasPackageAiMdFile(Path)} is {@code true} first.
     *
     * @param directory a directory that contains a {@code package.ai.md} file
     * @return the header of that package AI index file
     * @throws java.io.IOException if the file cannot be read
     */
    private AiMdHeader readChildPackageHeader(final Path directory) throws java.io.IOException {
        return documentCodec
                .read(directory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME))
                .header();
    }

    /**
     * Returns the lexicographically later of two ISO-8601 date strings,
     * i.e. the more-recent creation date.
     *
     * @param current   the currently known latest date
     * @param candidate a date from a child node to compare against {@code current}
     * @return {@code candidate} if it is strictly later than {@code current}, otherwise
     *         {@code current}
     */
    private String laterDate(final String current, final String candidate) {
        return candidate.compareTo(current) > 0 ? candidate : current;
    }

    /**
     * Returns {@code true} when {@code name} refers to a child AI index file that should
     * be included in content listings and checksum calculations. Excludes the
     * {@link AiMdHeaderCodec#PACKAGE_AI_MD_FILENAME} file itself and any
     * {@link AiMdHeaderCodec#GENERATED_BY_PREFIX} marker files.
     *
     * @param name file name of the path under examination
     * @return {@code true} if the file is a regular content AI index entry
     */
    private boolean isAiMdContentFile(final String name) {
        return !AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME.equals(name)
                && !name.startsWith(AiMdHeaderCodec.GENERATED_BY_PREFIX)
                && name.endsWith(AiMdHeaderCodec.AI_MD_EXTENSION);
    }
}
