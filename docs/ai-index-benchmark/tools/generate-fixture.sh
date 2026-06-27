#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Generates a large, *valid* synthetic Java class for benchmarking how the AI
# index plugin handles big source files (context sizing, trim/no-trim, output
# truncation, prefill/decode time on CPU).
#
# It models a fictional in-memory ledger (accounts, balances, transfers,
# weighted "bucket" scoring) so the file has genuine, varied content to
# summarize rather than dead filler.
#
# Usage:
#   docs/ai-index-benchmark/tools/generate-fixture.sh <target_kb> <output_file> [class_name]
#
# Examples:
#   generate-fixture.sh 100 build/fixture/BigFixture.java BigFixture
#   generate-fixture.sh 250 build/fixture/HugeFixture.java HugeFixture
#
# Notes:
# - The package is fixed to net.ladenthin.maven.llamacpp.aiindex.bigfixture; put
#   the output under .../bigfixture/ if you want javac to accept it (the plugin
#   itself only reads .java as text, so it does not require compilation).
# - Size is approximate: the script grows the method count until the file
#   reaches the requested size, then appends fixed tail methods.
set -euo pipefail

TARGET_KB="${1:?usage: generate-fixture.sh <target_kb> <output_file> [class_name]}"
OUT="${2:?usage: generate-fixture.sh <target_kb> <output_file> [class_name]}"
CLASS="${3:-BigFixture}"
TARGET_BYTES=$(( TARGET_KB * 1024 ))

mkdir -p "$(dirname "$OUT")"

header() {
cat <<HDR
// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex.bigfixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Large synthetic fixture (target ~${TARGET_KB} KB) used ONLY to benchmark how the
 * AI index plugin handles big source files. It models a fictional in-memory
 * ledger of accounts and transactions: it stores balances, applies
 * deposits/withdrawals/transfers, computes aggregates, and exposes many small
 * accessors so the file is genuinely large and varied.
 *
 * <p>This is not production code; it exists to exercise context sizing.</p>
 */
public final class ${CLASS} {

    /** Maximum number of accounts this ledger tracks. */
    public static final int MAX_ACCOUNTS = 4096;

    /** Default starting balance applied to a freshly opened account. */
    public static final long DEFAULT_BALANCE = 1000L;

    private final Map<Integer, Long> balances = new HashMap<>();
    private final Map<Integer, String> owners = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();
    private long totalVolume;
    private int transactionCount;
HDR
}

# A single bucket method (bug-free: every identifier is a real local/field).
method() {
  local i="$1"
  local w=$(( (i % 40) + 1 ))
  cat <<M

    /**
     * Adjusts the running score for bucket $i by combining the supplied delta
     * with weight$w. Returns the new partial score; negative inputs are clamped
     * to zero before the weight is applied.
     *
     * @param delta raw increment for bucket $i
     * @return the weighted, non-negative partial score for bucket $i
     */
    public double adjustBucket$i(final double delta) {
        final double clamped = delta < 0.0d ? 0.0d : delta;
        final double weighted = clamped * weight$w;
        this.totalVolume += (long) weighted;
        this.transactionCount++;
        if (weighted > weight$w) {
            auditLog.add("bucket$i exceeded base weight: " + weighted);
        }
        return weighted + (double) ($i % 7);
    }
M
}

tail_methods() {
cat <<'TAIL'

    /**
     * Opens a new account with the default balance, recording its owner and an
     * audit entry. Rejects ids outside the valid range or already in use.
     *
     * @param id    unique account id in [0, MAX_ACCOUNTS)
     * @param owner human-readable owner name
     * @return true if the account was created, false if rejected
     */
    public boolean openAccount(final int id, final String owner) {
        if (id < 0 || id >= MAX_ACCOUNTS) {
            return false;
        }
        if (balances.containsKey(id)) {
            return false;
        }
        balances.put(id, DEFAULT_BALANCE);
        owners.put(id, owner == null ? "unknown" : owner);
        auditLog.add("opened " + id + " for " + owners.get(id));
        return true;
    }

    /**
     * Transfers an amount between two existing accounts when the source holds
     * sufficient funds. Updates the running totals and appends an audit entry.
     *
     * @param from   source account id
     * @param to     destination account id
     * @param amount strictly positive amount to move
     * @return true on success, false on any validation failure
     */
    public boolean transfer(final int from, final int to, final long amount) {
        if (amount <= 0L) {
            return false;
        }
        final Long src = balances.get(from);
        final Long dst = balances.get(to);
        if (src == null || dst == null || src < amount) {
            return false;
        }
        balances.put(from, src - amount);
        balances.put(to, dst + amount);
        this.totalVolume += amount;
        this.transactionCount++;
        auditLog.add("transfer " + amount + " from " + from + " to " + to);
        return true;
    }

    /**
     * Computes the sum of all account balances currently tracked.
     *
     * @return total balance across every open account
     */
    public long totalBalance() {
        long sum = 0L;
        for (final Long b : balances.values()) {
            sum += b == null ? 0L : b;
        }
        return sum;
    }

    /** @return the number of transactions applied so far */
    public int getTransactionCount() {
        return transactionCount;
    }

    /** @return the cumulative monetary volume processed */
    public long getTotalVolume() {
        return totalVolume;
    }

    /** @return an immutable snapshot count of audit entries */
    public int auditSize() {
        return auditLog.size();
    }
}
TAIL
}

# Build the file: header + 40 weight fields + as many bucket methods as needed.
{
  header
  for i in $(seq 1 40); do
    echo "    /** Tunable weight number $i used by the scoring helpers below. */"
    echo "    private double weight$i = ${i}.0d;"
  done
} > "$OUT"

i=1
# Grow until the body (without the ~90-line tail) reaches the target size.
while [ "$(wc -c < "$OUT")" -lt "$TARGET_BYTES" ]; do
  method "$i" >> "$OUT"
  i=$(( i + 1 ))
done
tail_methods >> "$OUT"

echo "wrote $OUT"
echo "class=$CLASS methods=$(( i - 1 )) lines=$(wc -l < "$OUT") bytes=$(wc -c < "$OUT") (~$(( $(wc -c < "$OUT") / 1024 )) KB)"
