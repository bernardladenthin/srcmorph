// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.ladenthin.srcmorph.indexer.AiIndexOutcome;
import net.ladenthin.srcmorph.provider.AiGenerationUsage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class RunRecordTest {

    @TempDir
    Path tempDir;

    private static RunRecord runRecord() {
        // the run starts at 1 s on its clock and is written at 5.5 s; two files were skipped
        final long[] now = {1_000_000_000L};
        final RunRecord runRecord = new RunRecord("9.9.9", 2, () -> now[0]);
        now[0] = 6_500_000_000L;
        runRecord.addModelLoad("m", 750_000_000L);
        runRecord.addModelLoad("m", 250_000_000L);
        runRecord.addModelLoad("other", 0L);
        runRecord.add(new RunRecord.Entry(
                "src/A.java",
                "java",
                "file-body-java",
                "m",
                AiIndexOutcome.WRITTEN,
                new AiGenerationUsage(2, 100, 20, 80, "stop"),
                1_500_000_000L,
                "AAAA0000",
                "BBBB1111"));
        runRecord.add(new RunRecord.Entry(
                "src/B.java",
                "java",
                "file-body-java",
                "m",
                AiIndexOutcome.UNCHANGED,
                AiGenerationUsage.NONE,
                500_000_000L,
                "AAAA0000",
                ""));
        runRecord.add(new RunRecord.Entry(
                "src/C.sql",
                "sql",
                "file-body-sql",
                "n",
                AiIndexOutcome.FAILED,
                new AiGenerationUsage(1, 50, 7, 0, "length"),
                1_000_000_000L,
                "CCCC2222",
                ""));
        return runRecord;
    }

    @Test
    public void toJson_oneObjectPerFile() throws Exception {
        final JsonNode root = new ObjectMapper().readTree(runRecord().toJson());

        assertThat(root.get("pluginVersion").asText(), is(equalTo("9.9.9")));
        final JsonNode a = root.get("files").get(0);
        assertThat(a.get("file").asText(), is(equalTo("src/A.java")));
        assertThat(a.get("rule").asText(), is(equalTo("java")));
        assertThat(a.get("promptKey").asText(), is(equalTo("file-body-java")));
        assertThat(a.get("model").asText(), is(equalTo("m")));
        assertThat(a.get("outcome").asText(), is(equalTo("written")));
        assertThat(a.get("calls").asInt(), is(2));
        assertThat(a.get("promptTokens").asLong(), is(100L));
        assertThat(a.get("cachedPromptTokens").asLong(), is(80L));
        assertThat(a.get("completionTokens").asLong(), is(20L));
        assertThat(a.get("finishReason").asText(), is(equalTo("stop")));
        assertThat(a.get("seconds").asDouble(), is(1.5d));
        assertThat(a.get("promptHash").asText(), is(equalTo("AAAA0000")));
        assertThat(a.get("factsHash").asText(), is(equalTo("BBBB1111")));
        assertThat(root.get("files").get(1).get("outcome").asText(), is(equalTo("unchanged")));
        assertThat(root.get("files").get(2).get("outcome").asText(), is(equalTo("failed")));
    }

    @Test
    public void toJson_totals() throws Exception {
        final JsonNode totals =
                new ObjectMapper().readTree(runRecord().toJson()).get("totals");

        assertThat(totals.get("files").asInt(), is(3));
        assertThat(totals.get("written").asInt(), is(1));
        assertThat(totals.get("unchanged").asInt(), is(1));
        assertThat(totals.get("failed").asInt(), is(1));
        assertThat(totals.get("calls").asInt(), is(3));
        assertThat(totals.get("promptTokens").asLong(), is(150L));
        assertThat(totals.get("cachedPromptTokens").asLong(), is(80L));
        assertThat(totals.get("completionTokens").asLong(), is(27L));
        assertThat(totals.get("seconds").asDouble(), is(3.0d));
        assertThat(totals.get("skipped").asInt(), is(2));
        assertThat(totals.get("wallSeconds").asDouble(), is(5.5d));
        // a model that loaded twice adds up; one that loaded in no time is not listed
        assertThat(totals.get("modelLoadSeconds").toString(), is("{\"m\":1.0}"));
    }

    @Test
    public void toJson_totals_countUnchangedAndFailedApart() throws Exception {
        final RunRecord runRecord = runRecord();
        runRecord.add(new RunRecord.Entry(
                "src/D.java",
                "java",
                "file-body-java",
                "m",
                AiIndexOutcome.UNCHANGED,
                AiGenerationUsage.NONE,
                0L,
                "AAAA0000",
                ""));

        final JsonNode totals = new ObjectMapper().readTree(runRecord.toJson()).get("totals");

        assertThat(totals.get("unchanged").asInt(), is(2));
        assertThat(totals.get("failed").asInt(), is(1));
    }

    @Test
    public void write_createsTheDirectory() throws Exception {
        final Path file = tempDir.resolve("records/run.json");

        runRecord().write(file);

        assertThat(
                new String(Files.readAllBytes(file), StandardCharsets.UTF_8),
                is(equalTo(runRecord().toJson())));
    }

    @Test
    public void entries_areInOrderAndUnmodifiable() {
        final RunRecord runRecord = runRecord();

        assertThat(runRecord.entries().size(), is(3));
        assertThat(runRecord.entries().get(2).file(), is(equalTo("src/C.sql")));
        assertThat(runRecord.entries().get(2).outcome(), is(AiIndexOutcome.FAILED));
        assertThat(runRecord.entries().get(0).usage().calls(), is(2));
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class, () -> runRecord.entries().clear());
    }
}
