// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The user info of a URL ({@code user:password@} after the scheme and before the host), which srcmorph
 * never sends ({@code HttpURLConnection} does not authenticate with it) and never prints, nor the URL's
 * query or fragment, where a key can stand too.
 */
public final class UrlUserInfo {

    /**
     * A URL in a text: its scheme with {@code ://}, then everything up to a whitespace or a double quote
     * (the end of a JSON or YAML string). Possessive, so a URL is read once.
     */
    private static final Pattern URL = Pattern.compile("([A-Za-z][A-Za-z0-9+.-]{0,20}://)([^\\s\"]*+)");

    /** What a URL's user info, or everything after its scheme, is printed as. */
    private static final String MASK = "***";

    /** The character that ends a URL's user info. */
    private static final char USER_INFO_END = '@';

    /** The character that ends a URL's authority: the first one of its path. */
    private static final char PATH_START = '/';

    /**
     * The query or the fragment of a URL, from its {@code ?} or {@code #} to the end of the URL: a key can
     * stand there ({@code ?api-key=...}). Both repetitions are possessive and none can give back.
     */
    private static final Pattern URL_QUERY =
            Pattern.compile("([A-Za-z][A-Za-z0-9+.-]{0,20}://[^?#\\s\"']*+)([?#])[^\\s\"']*+");

    /** What a URL's query or fragment is printed as: the URL up to it, its mark, then this. */
    private static final String MASKED_QUERY = "$1$2***";

    private UrlUserInfo() {
        // static utility
    }

    /**
     * Replaces the user info and the query or fragment of every URL in a text by {@code ***}, so a password
     * or a key written into a {@code baseUrl} reaches no log line and no message:
     * {@code https://u:p@host/v1?key=k} is printed as {@code https://***@host/v1?***}. The user info goes up
     * to the URL's last {@code @}, so a password may hold an {@code @}, {@code ?}, {@code #} or {@code '}
     * unencoded. An {@code @} after the URL's first {@code /} cannot be told from a password that holds an
     * unencoded {@code /} ({@code https://user:pa/ss@host/v1}), so such a URL is printed as its scheme and
     * {@code ***} only.
     *
     * @param text the text: a URL, a log line, a message or the CLI's configuration echo
     * @return the text with every URL's user info, query and fragment masked
     */
    public static String mask(final String text) {
        final Matcher url = URL.matcher(text);
        final StringBuilder masked = new StringBuilder(text.length());
        int copied = 0;
        while (url.find()) {
            final String rest = text.substring(url.start(2), url.end());
            final int userInfoEnd = rest.lastIndexOf(USER_INFO_END);
            if (userInfoEnd >= 0) {
                masked.append(text, copied, url.start(2)).append(MASK);
                if (rest.lastIndexOf(PATH_START, userInfoEnd) < 0) {
                    masked.append(rest, userInfoEnd, rest.length());
                }
                copied = url.end();
            }
        }
        masked.append(text, copied, text.length());
        return URL_QUERY.matcher(masked).replaceAll(MASKED_QUERY);
    }
}
