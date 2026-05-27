// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

/**
 * Canonical header model for a single {@code .ai.md} document.
 *
 * <p>The header contains only deterministic metadata fields. All AI-generated content
 * is stored in the document body (after the {@code ---} separator), keeping the header
 * machine-parseable without AI involvement.</p>
 *
 * <p>Field semantics:</p>
 * <ul>
 *   <li><b>title</b>: Display title of the node, usually the file name or logical package path.</li>
 *   <li><b>h</b>: Header format version. Used to detect incompatible header schema changes.</li>
 *   <li><b>c</b>: Checksum of the represented node state.
 *     <ul>
 *       <li>For {@code file}: CRC32 of the source file content.</li>
 *       <li>For {@code package}: deterministic CRC32 derived from the direct child entries,
 *           ordered ascending by child name/path.</li>
 *     </ul>
 *   </li>
 *   <li><b>d</b>: Source date of the represented node state.
 *     <ul>
 *       <li>For {@code file}: last modification timestamp of the source file.</li>
 *       <li>For {@code package}: newest {@code d} value of the direct child entries.</li>
 *     </ul>
 *   </li>
 *   <li><b>t</b>: Timestamp when this {@code .ai.md} document was last generated.</li>
 *   <li><b>g</b>: Version of the template/generator implementation that produced this document.</li>
 *   <li><b>a</b>: Version of the AI summarization logic or AI output schema applied to this document.</li>
 *   <li><b>x</b>: Node type, for example {@code file} or {@code package}.</li>
 * </ul>
 *
 * <p>Important invariants:</p>
 * <ul>
 *   <li>Header comparison for rewrite decisions is based on all structural fields:
 *       {@code h}, {@code c}, {@code d}, {@code g}, {@code a}, {@code x}, and {@code title}.</li>
 *   <li>Package aggregation must be deterministic. Child traversal order must therefore be stable,
 *       typically ascending by child file name or relative path.</li>
 *   <li>The generator should preserve the body when the structural state did not change.</li>
 * </ul>
 *
 */
@ConvertToRecord
public final class AiMdHeader {
    private final String title;
    private final String h;
    private final String c;
    private final String d;
    private final String t;
    private final String g;
    private final String a;
    private final String x;

    /**
     * Creates a new {@link AiMdHeader}.
     *
     * @param title display title (typically the file name or package path)
     * @param h     header format version
     * @param c     source-state checksum
     * @param d     source-state timestamp
     * @param t     generation timestamp of this {@code .ai.md}
     * @param g     generator/template version
     * @param a     AI summarisation logic / output schema version
     * @param x     node type, for example {@code file} or {@code package}
     */
    public AiMdHeader(String title, String h, String c, String d, String t, String g, String a, String x) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(h, "h");
        Objects.requireNonNull(c, "c");
        Objects.requireNonNull(d, "d");
        Objects.requireNonNull(t, "t");
        Objects.requireNonNull(g, "g");
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(x, "x");
        this.title = title;
        this.h = h;
        this.c = c;
        this.d = d;
        this.t = t;
        this.g = g;
        this.a = a;
        this.x = x;
    }

    /**
     * Returns the display title.
     *
     * @return display title
     */
    public String title() {
        return title;
    }

    /**
     * Returns the header format version.
     *
     * @return header format version
     */
    public String h() {
        return h;
    }

    /**
     * Returns the source-state checksum.
     *
     * @return source-state checksum
     */
    public String c() {
        return c;
    }

    /**
     * Returns the source-state timestamp.
     *
     * @return source-state timestamp
     */
    public String d() {
        return d;
    }

    /**
     * Returns the generation timestamp of this {@code .ai.md}.
     *
     * @return generation timestamp
     */
    public String t() {
        return t;
    }

    /**
     * Returns the generator/template version.
     *
     * @return generator/template version
     */
    public String g() {
        return g;
    }

    /**
     * Returns the AI summarisation logic version.
     *
     * @return AI summarisation logic version
     */
    public String a() {
        return a;
    }

    /**
     * Returns the node type ({@code file} or {@code package}).
     *
     * @return node type
     */
    public String x() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiMdHeader that = (AiMdHeader) obj;
        return Objects.equals(this.title, that.title)
                && Objects.equals(this.h, that.h)
                && Objects.equals(this.c, that.c)
                && Objects.equals(this.d, that.d)
                && Objects.equals(this.t, that.t)
                && Objects.equals(this.g, that.g)
                && Objects.equals(this.a, that.a)
                && Objects.equals(this.x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, h, c, d, t, g, a, x);
    }

    @Override
    public String toString() {
        return "AiMdHeader[" + "title="
                + title + ", " + "h="
                + h + ", " + "c="
                + c + ", " + "d="
                + d + ", " + "t="
                + t + ", " + "g="
                + g + ", " + "a="
                + a + ", " + "x="
                + x + ']';
    }
}
