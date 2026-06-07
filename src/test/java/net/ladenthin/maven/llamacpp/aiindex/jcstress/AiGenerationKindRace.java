// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.jcstress;

import net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationKind;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.ZZ_Result;

@JCStressTest
@Description("Two threads reading enum constants must always see the expected values.")
@Outcome(id = "true, true", expect = Expect.ACCEPTABLE, desc = "Both readers see the correct enum constants")
@Outcome(
        id = {"true, false", "false, true", "false, false"},
        expect = Expect.FORBIDDEN,
        desc = "BUG: enum constant read unexpectedly")
@State
public class AiGenerationKindRace {

    @Actor
    public void actor1(ZZ_Result r) {
        r.r1 = AiGenerationKind.FILE_SUMMARY == AiGenerationKind.FILE_SUMMARY;
    }

    @Actor
    public void actor2(ZZ_Result r) {
        r.r2 = AiGenerationKind.PACKAGE_SUMMARY == AiGenerationKind.PACKAGE_SUMMARY;
    }
}
