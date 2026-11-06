// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.regex.Pattern;

/**
 * The user info of a URL ({@code user:password@} after the scheme and before the host), which srcmorph
 * never sends ({@code HttpURLConnection} does not authenticate with it) and never prints.
 */
public final class UrlUserInfo {

    /**
     * The user info after a URL's scheme, up to its last {@code @} before the host: of any length (a token
     * can be long), and with an {@code @} the password itself holds unencoded. The one greedy repetition
     * reads the authority and gives back to its last {@code @}, so a URL is read at most twice.
     */
    private static final Pattern URL_USER_INFO = Pattern.compile("([A-Za-z][A-Za-z0-9+.-]{0,20}://)[^/?#\\s\"']*@");

    /** What a URL's user info is printed as: the scheme, then this, then the host. */
    private static final String MASKED_USER_INFO = "$1***@";

    private UrlUserInfo() {
        // static utility
    }

    /**
     * Replaces the user info of every URL in a text by {@code ***}, so a password written into a
     * {@code baseUrl} reaches no log line and no message: {@code https://u:p@host/v1} is printed as
     * {@code https://***@host/v1}.
     *
     * @param text the text: a URL, a log line, a message or the CLI's configuration echo
     * @return the text with every URL's user info masked
     */
    public static String mask(final String text) {
        return URL_USER_INFO.matcher(text).replaceAll(MASKED_USER_INFO);
    }
}
