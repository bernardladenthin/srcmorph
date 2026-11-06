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
    public void mask_anAtInThePathOrAMail_staysAsItIs() {
        // arrange
        final String text = "https://h/v1/@scope/pkg and mail me@example.org";

        // act, assert
        assertThat(UrlUserInfo.mask(text), is(text));
    }

    @Test
    public void mask_aQueryOrAFragment_isMaskedFromItsMark() {
        // act, assert: an @ in the query is no user info; the query goes whole, the fragment too
        assertThat(
                UrlUserInfo.mask("https://h/v1/@scope/pkg?by=a@b#x@y and mail me@example.org"),
                is("https://h/v1/@scope/pkg?*** and mail me@example.org"));
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="carriesUserInfo">
    @Test
    public void carriesUserInfo_aUrlWithAndWithout_tellsThemApart() {
        // act, assert: a query with an @ is no user info
        assertThat(UrlUserInfo.carriesUserInfo("https://u:p@h/v1"), is(true));
        assertThat(UrlUserInfo.carriesUserInfo("https://h/v1?by=a@b"), is(false));
        assertThat(UrlUserInfo.carriesUserInfo("https://h/v1"), is(false));
    }
    // </editor-fold>
}
