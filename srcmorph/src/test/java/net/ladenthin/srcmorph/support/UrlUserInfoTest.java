// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class UrlUserInfoTest {

    @Test
    public void mask_urlsWithUserInfo_masksTheUserInfoOfEveryUrl() {
        // act
        final String masked = UrlUserInfo.mask("baseUrl: \"https://u:p@h:8443/v1\"\nother: http://key@127.0.0.1/x");

        // assert
        assertThat(masked, is("baseUrl: \"https://***@h:8443/v1\"\nother: http://***@127.0.0.1/x"));
    }

    @Test
    public void mask_anAtInThePathOrTheQueryOrAMail_staysAsItIs() {
        // arrange
        final String text = "https://h/v1/@scope/pkg?by=a@b#x@y and mail me@example.org";

        // act + assert
        assertThat(UrlUserInfo.mask(text), is(text));
    }
}
