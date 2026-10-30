// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import net.ladenthin.srcmorph.provider.RemoteModel;
import org.jspecify.annotations.Nullable;

/**
 * What {@link ListModelsEngine} found: per server the models it lists, rendered by {@link #render()} as
 * YAML a configuration can take over, a table of the models as comments and one {@code aiDefinitions}
 * entry per model.
 *
 * <p>An entry is a copy of the server's pattern definition with its own {@code key}
 * ({@code <pattern key>-<model id>}) and {@code remoteModel}. It carries the connection
 * ({@code provider}, {@code baseUrl}, {@code apiKeyEnv}, {@code stream}, {@code requestsPerMinute}), the
 * window and the two knobs
 * every summary run sets ({@code maxOutputTokens}, {@code temperature}), and {@code reasoningEffort}
 * when the pattern changes it. {@code contextSize} is the pattern's, lowered to the model's limit when
 * the server names a smaller one. Other sampling knobs of the pattern are not copied.</p>
 *
 * <p>Hand-rolled rather than written by a YAML library, like {@link CalibrationReport}: this module
 * carries none, and the document is small and fixed.</p>
 */
@ToString
@EqualsAndHashCode
public final class ModelListReport {

    /** The text of a table cell whose value the server does not state. */
    static final String UNKNOWN = "-";

    /** The heading of the id column. */
    private static final String MODEL_HEADING = "model";

    /** Width of the context column. */
    private static final int CONTEXT_WIDTH = 9;

    /** Width of each price column. */
    private static final int PRICE_WIDTH = 10;

    /** Joins the pattern key and the model id into the key of a template entry. */
    static final String KEY_SEPARATOR = "-";

    private final String filter;
    private final List<Listing> listings;

    /**
     * Creates a new {@link ModelListReport}.
     *
     * @param filter   the filter the models were chosen with, blank for none
     * @param listings one listing per server
     */
    public ModelListReport(final String filter, final Collection<Listing> listings) {
        this.filter = Objects.requireNonNull(filter, "filter");
        this.listings = new ArrayList<>(listings);
    }

    /**
     * Returns the listings, one per server.
     *
     * @return an unmodifiable view of the listings
     */
    public List<Listing> listings() {
        return Collections.unmodifiableList(listings);
    }

    /**
     * Renders the report as YAML: per server a comment block with the model table, then one
     * {@code aiDefinitions} list with an entry per listed model ({@code aiDefinitions: []} when none
     * is left). The whole text is valid YAML; indented to the level of {@code srcMorph}'s fields, its
     * entries go into a configuration's own {@code aiDefinitions}.
     *
     * @return the YAML document
     */
    public String render() {
        final StringBuilder out = new StringBuilder();
        boolean empty = true;
        for (final Listing listing : listings) {
            appendTable(out, listing);
            empty &= listing.models().isEmpty();
        }
        if (empty) {
            return out.append("aiDefinitions: []\n").toString();
        }
        out.append("aiDefinitions:\n");
        for (final Listing listing : listings) {
            for (final RemoteModel model : listing.models()) {
                appendEntry(out, listing, model);
            }
        }
        return out.toString();
    }

    private void appendTable(final StringBuilder out, final Listing listing) {
        out.append("# aiDefinition '")
                .append(listing.key())
                .append("': ")
                .append(listing.baseUrl())
                .append(", ")
                .append(listing.listed())
                .append(" models");
        final String shownFilter = filter.trim();
        final List<RemoteModel> models = listing.models();
        if (!shownFilter.isEmpty()) {
            out.append(", ")
                    .append(models.size())
                    .append(" matching '")
                    .append(shownFilter)
                    .append('\'');
        }
        out.append('\n');
        if (models.isEmpty()) {
            out.append("#\n");
            return;
        }
        int idWidth = MODEL_HEADING.length();
        for (final RemoteModel model : models) {
            idWidth = Math.max(idWidth, model.id().length());
        }
        final String row =
                "#   %-" + idWidth + "s %" + CONTEXT_WIDTH + "s %" + PRICE_WIDTH + "s %" + PRICE_WIDTH + "s\n";
        out.append(String.format(Locale.ROOT, row, MODEL_HEADING, "context", "USD/M in", "USD/M out"));
        for (final RemoteModel model : models) {
            out.append(String.format(
                    Locale.ROOT,
                    row,
                    model.id(),
                    model.contextLength() > 0 ? Integer.toString(model.contextLength()) : UNKNOWN,
                    price(model.inputUsdPerMillion()),
                    price(model.outputUsdPerMillion())));
        }
        out.append("#\n");
    }

    private static String price(final @Nullable BigDecimal usd) {
        return usd == null ? UNKNOWN : usd.toPlainString();
    }

    private static void appendEntry(final StringBuilder out, final Listing listing, final RemoteModel model) {
        out.append("  - key: ")
                .append(quote(listing.key() + KEY_SEPARATOR + model.id()))
                .append('\n');
        appendField(out, "provider", quote(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE));
        appendField(out, "baseUrl", quote(listing.baseUrl()));
        appendField(out, "remoteModel", quote(model.id()));
        if (!listing.apiKeyEnv().trim().isEmpty()) {
            appendField(out, "apiKeyEnv", quote(listing.apiKeyEnv()));
        }
        if (listing.stream()) {
            appendField(out, "stream", "true");
        }
        if (listing.requestsPerMinute() > 0) {
            appendField(out, "requestsPerMinute", Integer.toString(listing.requestsPerMinute()));
        }
        appendField(out, "contextSize", Integer.toString(contextSize(listing.contextSize(), model.contextLength())));
        appendField(out, "maxOutputTokens", Integer.toString(listing.maxOutputTokens()));
        appendField(out, "temperature", Float.toString(listing.temperature()));
        if (!AiGenerationConfig.DEFAULT_REASONING_EFFORT.equals(listing.reasoningEffort())) {
            appendField(out, "reasoningEffort", quote(listing.reasoningEffort()));
        }
    }

    /**
     * The {@code contextSize} of a template entry: the pattern's, or the model's limit when the server
     * names a smaller one.
     *
     * @param patternContextSize the pattern definition's {@code contextSize}
     * @param modelLimit         the model's limit, {@link RemoteModel#UNKNOWN_CONTEXT_LENGTH} when unknown
     * @return the window of the entry
     */
    static int contextSize(final int patternContextSize, final int modelLimit) {
        return modelLimit > RemoteModel.UNKNOWN_CONTEXT_LENGTH
                ? Math.min(modelLimit, patternContextSize)
                : patternContextSize;
    }

    private static void appendField(final StringBuilder out, final String name, final String value) {
        out.append("    ").append(name).append(": ").append(value).append('\n');
    }

    /**
     * A double-quoted YAML scalar: backslash and double quote escaped, so any model id or URL reads
     * back unchanged.
     *
     * @param text the text
     * @return the quoted scalar
     */
    static String quote(final String text) {
        return '"' + text.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    /**
     * One server's models and the pattern definition its template entries copy.
     */
    @ToString
    @EqualsAndHashCode
    public static final class Listing {
        private final String key;
        private final String baseUrl;
        private final String apiKeyEnv;
        private final boolean stream;
        private final int requestsPerMinute;
        private final int contextSize;
        private final int maxOutputTokens;
        private final float temperature;
        private final String reasoningEffort;
        private final int listed;
        private final List<RemoteModel> models;

        private Listing(
                final String key,
                final AiGenerationConfig pattern,
                final int listed,
                final Collection<RemoteModel> models) {
            this.key = Objects.requireNonNull(key, "key");
            this.baseUrl = pattern.getBaseUrl().trim();
            this.apiKeyEnv = pattern.getApiKeyEnv();
            this.stream = pattern.isStream();
            this.requestsPerMinute = pattern.getRequestsPerMinute();
            this.contextSize = pattern.getContextSize();
            this.maxOutputTokens = pattern.getMaxOutputTokens();
            this.temperature = pattern.getTemperature();
            this.reasoningEffort = pattern.getReasoningEffort();
            this.listed = listed;
            this.models = new ArrayList<>(models);
        }

        /**
         * Creates the listing of one server.
         *
         * @param key     the key of the pattern definition
         * @param pattern the pattern definition's configuration
         * @param listed  how many models the server listed before the filter
         * @param models  the models left after the filter
         * @return the listing
         */
        static Listing of(
                final String key,
                final AiGenerationConfig pattern,
                final int listed,
                final Collection<RemoteModel> models) {
            return new Listing(key, pattern, listed, models);
        }

        /**
         * Returns the key of the pattern definition.
         *
         * @return the key
         */
        public String key() {
            return key;
        }

        /**
         * Returns the server's base URL, as the pattern names it.
         *
         * @return the base URL
         */
        public String baseUrl() {
            return baseUrl;
        }

        /**
         * Returns the name of the variable holding the server's key.
         *
         * @return the variable name, empty when the pattern sends no key
         */
        public String apiKeyEnv() {
            return apiKeyEnv;
        }

        /**
         * Returns whether the pattern streams.
         *
         * @return the pattern's {@code stream}
         */
        public boolean stream() {
            return stream;
        }

        /**
         * Returns the pattern's request limit.
         *
         * @return the pattern's {@code requestsPerMinute}; {@code 0} or less for none
         */
        public int requestsPerMinute() {
            return requestsPerMinute;
        }

        /**
         * Returns the pattern's window.
         *
         * @return the pattern's {@code contextSize}
         */
        public int contextSize() {
            return contextSize;
        }

        /**
         * Returns the pattern's output budget.
         *
         * @return the pattern's {@code maxOutputTokens}
         */
        public int maxOutputTokens() {
            return maxOutputTokens;
        }

        /**
         * Returns the pattern's temperature.
         *
         * @return the pattern's {@code temperature}
         */
        public float temperature() {
            return temperature;
        }

        /**
         * Returns the pattern's reasoning effort.
         *
         * @return the pattern's {@code reasoningEffort}
         */
        public String reasoningEffort() {
            return reasoningEffort;
        }

        /**
         * Returns how many models the server listed before the filter.
         *
         * @return the count
         */
        public int listed() {
            return listed;
        }

        /**
         * Returns the models left after the filter, sorted by id.
         *
         * @return an unmodifiable view of the models
         */
        public List<RemoteModel> models() {
            return Collections.unmodifiableList(models);
        }
    }
}
