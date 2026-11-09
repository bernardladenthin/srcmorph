// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class UrlUserInfoTest {

    // <editor-fold defaultstate="collapsed" desc="mask">
    @Test
    public void mask_urlsWithUserInfo_masksTheUserInfoOfEveryUrl() {
        // act
        final String masked = UrlUserInfo.mask("baseUrl: \"https://u:p@h:8443/v1\"\nother: http://key@127.0.0.1/x");

        // assert
        assertThat(masked, is("baseUrl: \"https://***@h:8443/v1\"\nother: http://***@127.0.0.1/x"));
    }

    @Test
    public void mask_anAtAfterTheFirstSlash_masksEverythingAfterTheScheme() {
        // act, assert: an @ in the path cannot be told from a password that holds an unencoded /
        assertThat(
                UrlUserInfo.mask("https://h/v1/@scope/pkg and mail me@example.org"),
                is("https://*** and mail me@example.org"));
        assertThat(
                UrlUserInfo.mask("baseUrl: \"https://user:pa/ss@host/v1\", next"),
                is("baseUrl: \"https://***\", next"));
        assertThat(UrlUserInfo.mask("https://u:123/abc@host/v1 rest"), is("https://*** rest"));
    }

    @Test
    public void mask_aMailOrAnAtBehindAQuote_staysAsItIs() {
        // arrange: no scheme before the mail, and the URL ends at the quote before the @
        final String text = "mail me@example.org \"https://h/v1\"@x";

        // act, assert
        assertThat(UrlUserInfo.mask(text), is(text));
    }

    @Test
    public void mask_aQueryOrAFragment_isMaskedFromItsMark() {
        // act, assert: the query goes whole, the fragment too
        assertThat(UrlUserInfo.mask("https://h/v1/pkg?by=ab#xy and more"), is("https://h/v1/pkg?*** and more"));
        assertThat(UrlUserInfo.mask("'https://u:p@gw/v1?api-key=SECRET'"), is("'https://***@gw/v1?***'"));
        assertThat(UrlUserInfo.mask("https://gw/v1#k"), is("https://gw/v1#***"));
    }

    @Test
    public void mask_aUserInfoOfThreeHundredCharacters_isMasked() {
        // arrange: a token as long as a JWT
        final StringBuilder token = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            token.append('x');
        }
        final String url = "https://user:" + token + "@host/v1";

        // act, assert
        assertThat(UrlUserInfo.mask(url), is("https://***@host/v1"));
    }

    @Test
    public void mask_aPasswordWithAnAt_isMaskedWhole() {
        // act, assert: up to the last @ before the host, nothing of the password stays
        assertThat(UrlUserInfo.mask("https://u:p@ss@h/v1"), is("https://***@h/v1"));
    }

    @Test
    public void mask_aPasswordWithAQuoteOrAQueryOrAFragmentMark_isMaskedWhole() {
        // act, assert: characters a password may hold unencoded before the host
        assertThat(UrlUserInfo.mask("https://alice:it's@host/v1"), is("https://***@host/v1"));
        assertThat(UrlUserInfo.mask("https://alice:s3cr?t@host/v1"), is("https://***@host/v1"));
        assertThat(UrlUserInfo.mask("https://alice:se#cret@host/v1"), is("https://***@host/v1"));
    }

    @Test
    public void mask_anAtAtTheEndOfTheUrl_masksItsUserInfo() {
        // act, assert: no host after the @, no slash at all
        assertThat(UrlUserInfo.mask("https://key@"), is("https://***@"));
        assertThat(UrlUserInfo.mask("x https://@h"), is("x https://***@h"));
        // a slash right after the scheme, before the @
        assertThat(UrlUserInfo.mask("https:///@h"), is("https://***"));
    }
    // </editor-fold>
}
