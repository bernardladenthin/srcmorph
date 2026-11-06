// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.regex.Pattern;

/**
 * The user info of a URL ({@code user:password@} after the scheme and before the host), which srcmorph
 * never sends ({@code HttpURLConnection} does not authenticate with it) and never prints, nor the URL's
 * query or fragment, where a key can stand too.
 */
public final class UrlUserInfo {

    /**
     * The user info after a URL's scheme, up to its last {@code @} before the path: of any length (a token
     * can be long), and with an {@code @}, {@code ?}, {@code #} or {@code '} the password itself holds
     * unencoded. The one greedy repetition reads the authority and gives back to its last {@code @}, so a
     * URL is read at most twice.
     */
    private static final Pattern URL_USER_INFO = Pattern.compile("([A-Za-z][A-Za-z0-9+.-]{0,20}://)[^/\\s\"]*@");

    /** What a URL's user info is printed as: the scheme, then this, then the host. */
    private static final String MASKED_USER_INFO = "$1***@";

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
     * Returns whether a URL in a text carries user info ({@code user:password@} before the host).
     *
     * @param text the text, a {@code baseUrl}
     * @return {@code true} when a URL in it has user info
     */
    public static boolean carriesUserInfo(final String text) {
        return URL_USER_INFO.matcher(text).find();
    }

    /**
     * Replaces the user info and the query or fragment of every URL in a text by {@code ***}, so a password
     * or a key written into a {@code baseUrl} reaches no log line and no message:
     * {@code https://u:p@host/v1?key=k} is printed as {@code https://***@host/v1?***}.
     *
     * @param text the text: a URL, a log line, a message or the CLI's configuration echo
     * @return the text with every URL's user info, query and fragment masked
     */
    public static String mask(final String text) {
        return URL_QUERY
                .matcher(URL_USER_INFO.matcher(text).replaceAll(MASKED_USER_INFO))
                .replaceAll(MASKED_QUERY);
    }
}
