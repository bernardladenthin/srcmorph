# Profiles

A profile is one directory holding everything srcmorph's recognition depends on besides the model:
the prompts and the exact fact counters, one file per key. A configuration names the directory once
(`profileDirectory`), and its rules name only keys: a rule's `promptKey` reads
`<profileDirectory>/<key>.md`, its `factsKey` reads `<profileDirectory>/<key>.tsv`. No prompt text and
no regular expression is repeated in a configuration, so a profile can be copied, versioned and
handed on as a whole.

[`v3/`](v3/) is the current profile:

| File | Key | For |
|---|---|---|
| [`v3/file-body-oop.md`](v3/file-body-oop.md) | `promptKey: file-body-oop` | one source file of a class-based language: Java, Kotlin, Groovy, Scala; TypeScript (`.ts`, `.mts`, `.cts`) as well |
| [`v3/file-body-view.md`](v3/file-body-view.md) | `promptKey: file-body-view` | one server-side view: JSP, JSP fragment, tag file, Facelets |
| [`v3/file-body-contract.md`](v3/file-body-contract.md) | `promptKey: file-body-contract` | one API contract: OpenAPI, AsyncAPI, JSON Schema, Avro, Protobuf, GraphQL |
| [`v3/file-body-ops.md`](v3/file-body-ops.md) | `promptKey: file-body-ops` | one build, deploy or configuration file: CI, Dockerfile, Compose, Kubernetes, Helm, Ansible, Terraform |
| [`v3/file-body-sql.md`](v3/file-body-sql.md) | `promptKey: file-body-sql` | one SQL file, as schema |
| [`v3/file-body-shell.md`](v3/file-body-shell.md) | `promptKey: file-body-shell` | one Bash, POSIX sh or ksh93 script |
| [`v3/file-body-perl.md`](v3/file-body-perl.md) | `promptKey: file-body-perl` | one Perl script or module |
| [`v3/file-body-xml.md`](v3/file-body-xml.md) | `promptKey: file-body-xml` | one XML file: a POM, a Spring context, a Jakarta EE descriptor, a JPA, Hibernate or MyBatis mapping, an integration route, BPMN or DMN, WSDL, an XSD, an XSLT |
| [`v3/file-body-fallback.md`](v3/file-body-fallback.md) | `promptKey: file-body-fallback` | any other source file |
| [`v3/package-body.md`](v3/package-body.md) | `promptKey: package-body` | a package, from its file summaries |
| [`v3/project-body.md`](v3/project-body.md) | `promptKey: project-body` | the project overview, from the package leads |
| [`v3/map-chunk.md`](v3/map-chunk.md) | `mapPromptKey: map-chunk` | appended to a chunked rule's prompt in every map call |
| [`v3/reduce-parts.md`](v3/reduce-parts.md) | `reducePromptKey: reduce-parts` | appended to a chunked rule's prompt in every reduce call |
| [`v3/java-facts.tsv`](v3/java-facts.tsv) | `factsKey: java-facts` | exact counts in front of every Java summary |
| [`v3/sql-facts.tsv`](v3/sql-facts.tsv) | `factsKey: sql-facts` | exact counts in front of every SQL summary |
| [`v3/shell-facts.tsv`](v3/shell-facts.tsv) | `factsKey: shell-facts` | the same for Bash, POSIX sh and ksh93 scripts |
| [`v3/perl-facts.tsv`](v3/perl-facts.tsv) | `factsKey: perl-facts` | the same for Perl scripts and modules |
| [`v3/xml-facts.tsv`](v3/xml-facts.tsv) | `factsKey: xml-facts` | the same for XML, XSD and XSLT files |
| [`v3/pom-facts.tsv`](v3/pom-facts.tsv) | `factsKey: pom-facts` | the same for Maven POMs (a rule matching `**/*pom.xml`) |
| [`v3/kotlin-facts.tsv`](v3/kotlin-facts.tsv), [`v3/groovy-facts.tsv`](v3/groovy-facts.tsv), [`v3/scala-facts.tsv`](v3/scala-facts.tsv) | `factsKey: kotlin-facts`, `groovy-facts`, `scala-facts` | Kotlin; Groovy and Spock specifications; Scala 2 and 3 (with `file-body-oop`) |
| [`v3/java-ee-facts.tsv`](v3/java-ee-facts.tsv) | `factsKey: java-ee-facts` | `java-facts` plus the EJB, JPA, CDI, JAX-RS, JAX-WS, Servlet, JTA and security annotations |
| [`v3/jee-facts.tsv`](v3/jee-facts.tsv), [`v3/persistence-facts.tsv`](v3/persistence-facts.tsv), [`v3/mybatis-facts.tsv`](v3/mybatis-facts.tsv) | `factsKey: jee-facts`, `persistence-facts`, `mybatis-facts` | `web.xml`, `ejb-jar.xml`, `application.xml`; `persistence.xml`, `orm.xml`, `*.hbm.xml`; MyBatis mappers |
| [`v3/integration-facts.tsv`](v3/integration-facts.tsv), [`v3/bpmn-facts.tsv`](v3/bpmn-facts.tsv), [`v3/dmn-facts.tsv`](v3/dmn-facts.tsv), [`v3/wsdl-facts.tsv`](v3/wsdl-facts.tsv) | `factsKey: integration-facts`, `bpmn-facts`, `dmn-facts`, `wsdl-facts` | Camel, Spring Integration, Mule and Synapse routes; BPMN processes; DMN decisions (inputs, outputs, rules, hit policies); WSDL |
| [`v3/jsp-facts.tsv`](v3/jsp-facts.tsv), [`v3/facelets-facts.tsv`](v3/facelets-facts.tsv) | `factsKey: jsp-facts`, `facelets-facts` | JSP and tag files; Facelets views (with `file-body-view`) |
| [`v3/openapi-facts.tsv`](v3/openapi-facts.tsv), [`v3/asyncapi-facts.tsv`](v3/asyncapi-facts.tsv) | `factsKey: openapi-facts`, `asyncapi-facts` | OpenAPI and Swagger; AsyncAPI 2 and 3; YAML or JSON (with `file-body-contract`) |
| [`v3/proto-facts.tsv`](v3/proto-facts.tsv), [`v3/avro-facts.tsv`](v3/avro-facts.tsv), [`v3/graphql-facts.tsv`](v3/graphql-facts.tsv) | `factsKey: proto-facts`, `avro-facts`, `graphql-facts` | Protocol Buffers; Avro schemas (`.avsc`); GraphQL schemas and operations (with `file-body-contract`) |
| [`v3/k8s-facts.tsv`](v3/k8s-facts.tsv), [`v3/actions-facts.tsv`](v3/actions-facts.tsv), [`v3/dockerfile-facts.tsv`](v3/dockerfile-facts.tsv) | `factsKey: k8s-facts`, `actions-facts`, `dockerfile-facts` | Kubernetes and Helm; GitHub Actions; Dockerfiles (with `file-body-ops`) |
| [`v3/compose-facts.tsv`](v3/compose-facts.tsv), [`v3/ansible-facts.tsv`](v3/ansible-facts.tsv), [`v3/terraform-facts.tsv`](v3/terraform-facts.tsv), [`v3/camel-yaml-facts.tsv`](v3/camel-yaml-facts.tsv) | `factsKey: compose-facts`, `ansible-facts`, `terraform-facts`, `camel-yaml-facts` | Docker Compose; Ansible playbooks and roles; Terraform, OpenTofu and Terragrunt; Camel's YAML DSL (with `file-body-ops`) |
| [`v3/gradle-facts.tsv`](v3/gradle-facts.tsv), [`v3/properties-facts.tsv`](v3/properties-facts.tsv) | `factsKey: gradle-facts`, `properties-facts` | Gradle build and settings scripts, Groovy and Kotlin DSL; Java `.properties` files (with `file-body-ops`) |
| [`v3/powershell-facts.tsv`](v3/powershell-facts.tsv), [`v3/batch-facts.tsv`](v3/batch-facts.tsv) | `factsKey: powershell-facts`, `batch-facts` | PowerShell scripts and modules; Windows batch files (with `file-body-fallback`: no prompt of their own) |

## Prompt files

A `.md` file is the **system prompt** of its key, sent verbatim: no placeholders, no license header
(it would reach the model). srcmorph sends the file's path below the base directory (a package's
name) and the source (or the child summaries) as a separate user message, so two `web.xml` of
different modules read apart. Each file starts with a domain lead line, then fixed `####`
sections (Purpose, Type, Input, Output, Core logic, Public API, Dependencies, Exceptions, Concurrency
for Java; Schema, Reads, Writes, Relationships, Routine logic for SQL; Constants and configuration for
shell, Perl and other files; Definitions, Configuration and Dependencies for XML), with the rules against code fences, `###` headings and invented names, and the "index
only what is literally present" guard for truncated sources. Every file prompt has the same secrets
rule: a password, private key, token, API key or credentials in a URL is named, its value written as
`***`. A configuration keeps the files that hold credentials away from the model altogether with a
`skip` rule; the examples and the self-test carry one (`id: secrets`: `.env`, `*.tfvars`,
`*.tfstate`, Maven's `settings.xml`, keys, `*secret*` and `*credential*` configuration files).

`map-chunk.md` and `reduce-parts.md` (keys `map-chunk`, `reduce-parts`) are not prompts of their own: a rule's `mapPromptKey` and
`reducePromptKey` append them to its prompt (after a blank line) in the map and the reduce calls of
`mapReduce` and `sampleChunks`, so a call knows that it reads one chunk (`Chunk i of n:`) or the part
summaries to merge (`Part n:`), and the language's sections stay those of the rule's prompt.

## Fact files

A `.tsv` file holds one counter per line, its label and a Java regular expression separated by a tab;
lines starting with `#` are comments. srcmorph counts each pattern's matches over the whole source
and writes them as one line before the AI summary, for example
`**Facts (exact, whole file):** INSERT statements: 5; VALUES rows (approx): 33; tables: 2; ...`. A
sampled or chunked summary cannot count reliably, these lines can. Every pattern is compiled when the
run starts, so a broken one fails it with the file and line.

A line `[include]<TAB><key>` puts the counters of `<key>.tsv` of the profile directory at its place,
its `[ignore]` lines included: `jee-facts` is `xml-facts` plus the descriptor counters,
`java-ee-facts` is `java-facts` plus the annotations, so a kind that extends another adds its own
counters and copies none. An included file may include others; one that includes itself, also
through others, fails the run naming the chain, and so does a label that two of the files use. A
label must not start with `#`, which makes the line a comment.

A label starting with `[ignore]` marks a region instead of a counter: its matches are blanked out
before the other counters count: a region over several lines keeps only its line breaks, so `^`
anchors and line counts see the same lines as before, and a region within one line becomes one
space, so the words around it stay apart, or nothing at the start of a line, so the line still
starts with what follows it (`/* x */class A` counts for `^class`). That is how a sub named in Perl's POD, a function or a `set -e`
in a shell heredoc, a commented-out POM dependency or an `INSERT` inside a SQL block comment stays
out of the counts. An ignore line is not reported. The prefix may be written in any case; any other
label starting with `[` is refused as an unknown marker. In a configuration the same is a counter
with `ignore: true`, and its label must not start with `[`. A configuration's `facts` and a facts file
are held to the same rules: a label and a pattern on every counter, no label twice, and at least one
counter that is not an ignore counter.

Two more markers open a counter's label, in any order and case. `[raw]` counts the source as written,
before any ignore line blanked it: a marker that lives in comments, `java-facts`' `TODO/FIXME` and
PowerShell's `#Requires`, counts next to counters that skip comments. `[distinct]` counts the
different texts a pattern matched instead of its matches: the first group of the pattern where it
took part, the whole match otherwise, so twelve `@Inject Clock` fields are one injected type where a
plain counter counts twelve injection points. In a configuration they are `raw: true` and
`distinct: true`; an ignore counter is neither (it counts nothing). A summary written before these
markers existed keeps its facts hash: they enter it only where set.

Every pattern has a time limit, counted in characters the matcher reads rather than in seconds, so
it holds the same on every machine: 2,000 per character of the source plus 1,000,000. The patterns
here read at most about 830 per character, `java-facts`' field initializer (read up to 10,000
characters) over a file of nothing but `public a b=` lines, most of them a few
(`ProfileFactPatternsLinearityTest` asserts that none reaches the limit), so it stops only a
pattern that backtracks without end, a nested quantifier or a lazy scan for a closer that never
comes, in a counter of your own: that counter is left out of
the facts line, an ignore counter blanks nothing, and the run logs a warning naming it. A
backtracking pattern therefore costs at most that many reads per file instead of hanging the run.

Several ignore lines blank in file order, each over the text the one before left, so a region that
can hold another one's opener (a `<!--` inside CDATA, POD or `__END__` inside a Perl heredoc, a `/*`
inside a SQL string) would be cut at the wrong place. The files here therefore have one ignore line
per language, an alternation of all its regions: the region that starts first wins, as in a lexer.
Blanking leaves a space within a line, so `INSERT/* x */INTO` reads as `INSERT INTO`, and nothing at a
line start. A counter anchored at
a line start allows spaces and tabs there (`^[ \t]*`), never `\s*`: a blanked region leaves a run of
line breaks, and `^\s*` over one is quadratic (a 40 000-line block comment took 44 s, now 0.05 s;
`SqlFactPatternsTest` pins it). For the same reason a region whose end never comes runs to the end
of the file (an unclosed `/*`, a heredoc without its marker): a region that failed would be searched
again from every later opener. A shell or Perl `<<` that is a shift or sits in a string opens none
(20 000 lines of shifts, strings or unclosed heredocs took 25 to 207 s in shell and 95 to 223 s in
Perl, now at most 0.2 s), and the Java parameter list is read possessively, one level of parentheses
deep (the 6 000 constants of the large corpus's enum took 8 s, now 0.1 s); `ShellFactPatternsTest`, `PerlFactPatternsTest` and
`JavaStructureFactPatternsTest` pin these. `ProfileFactPatternsLinearityTest` measures all of it for
every pattern of every file here: it repeats 110 adversarial lines (unclosed openers and tags,
heredoc markers, unbalanced parentheses, runs of one character) and counts the characters the
matcher reads, which may grow at most six times for four times the input. It found six quadratic
patterns in the 1.4.0 files (the XML and POM regions, the XSD counter, the Java type arguments and
field initializer, the shell option arms), all fixed, and measures a new file without a change.

The counters are regex heuristics, and their labels say so where they are approximate. The tests read
these files, not copies, and count through the production extractor, so the ignore lines apply:
`SqlFactPatternsTest` pins every SQL counter on PostgreSQL, MySQL, T-SQL and Oracle forms (including
the known over- and undercount of `VALUES rows (approx)`), `JavaStructureFactPatternsTest` the
boundary of the Java type declaration, method, constructor and field counts, and `ShellFactPatternsTest`,
`PerlFactPatternsTest` and `XmlFactPatternsTest` each counter's forms and near misses plus the
counts of the benchmark's language corpus ([`corpus-languages/`](../docs/ai-index-benchmark/corpus-languages/)).

| `sql-facts` | Counts |
|---|---|
| `[ignore] comments and string literals` | `-- ...` comments, `'...'` literals and `/* ... */` comments, leftmost first, an unclosed `/*` to the end of the file; not MySQL's `/*! ... */` or Oracle's `/*+ ... */`, which hold SQL. Also, so an apostrophe in them opens no literal: PostgreSQL's dollar quoting on one line (a multi-line function body stays), Oracle's `q'[...]'`, a `"..."` holding an apostrophe (a quoted identifier stays) and MySQL's `#` comments (not a T-SQL `#temp`). Not seen as written: PostgreSQL's `'C:\'` (the backslash escapes) and nested comments (one ends at its first `*/`) |
| `INSERT statements` | `INSERT [INTO]` at the start of a line, also MySQL's `IGNORE` / `LOW_PRIORITY`, an Oracle hint, `INSERT ALL` / `FIRST` and `REPLACE INTO`; not a `MERGE` branch or a trigger's event list |
| `VALUES rows (approx)` | the row tuples of `VALUES` lists, so a multi-row `INSERT` counts each row, not MySQL's `VALUES(col)` of `ON DUPLICATE KEY UPDATE`; rows an `INSERT ... SELECT` reads from a table are not seen |
| `tables`, `views`, `indexes` | `CREATE` statements, also `TEMP`/`UNLOGGED`/`FOREIGN` and MariaDB's `OR REPLACE` tables, `MATERIALIZED`, `OR REPLACE`/`OR ALTER`, MySQL's `ALGORITHM` / `DEFINER` / `SQL SECURITY` and Oracle's `FORCE EDITIONABLE` views (not a `MATERIALIZED VIEW LOG`), `UNIQUE`/`CLUSTERED`/`COLUMNSTORE`/`FULLTEXT`/`SPATIAL`/`BITMAP` indexes |
| `functions/procedures`, `triggers` | `CREATE [OR REPLACE / OR ALTER] [EDITIONABLE] FUNCTION / PROCEDURE / PROC / TRIGGER`, also MySQL's `DEFINER=` |
| `types` | `CREATE [OR REPLACE] [EDITIONABLE] TYPE` (enums, composite and object types; not a `TYPE BODY`) |
| `ALTER TABLE` | every `ALTER TABLE` statement |
| `sequences`, `extensions`, `schemas and databases`, `packages and package bodies` | `CREATE [TEMP] SEQUENCE`, `CREATE EXTENSION`, `CREATE SCHEMA` / `DATABASE` (not a `DATABASE LINK` or `SCOPED CREDENTIAL`), Oracle's `CREATE [OR REPLACE] PACKAGE [BODY]` |

| `shell-facts` (Bash, sh, ksh93) | Counts |
|---|---|
| `[ignore]` comments and heredoc bodies | a `#` comment, a whole line or after a blank; a heredoc from `<<` to its end marker, any word quoted or not (`<<EOF`, `<<'END-OF-SQL'`, `<<!`), the marker alone on its line, after tabs for `<<-`, without one to the end of the file (as bash reads it); not a shift inside `(( ))`, `$(( ))` or `$[ ]` (also after up to three groups in parentheses, `$(( (a) + ((b)) << n ))`), nor a `<<` whose line goes on with an unclosed quote (inside a string) |
| `functions` | `name()`, `name ()` and `function name` definitions, with any compound command as the body (`{`, `(`, `if`, `for`, `while`, `until`, `case`, `select`, `[[`) |
| `getopts loops`, `getopts option letters`, `dash-option case arms` | `getopts` calls; each letter of a literal option string; `case` arms of `-x` / `--long` options, as a hand-written option loop has them |
| `named positional parameters` | an argument assigned to a name (`SRC="$1"`, `local mode=${3:-fast}`) |
| `exported variables`, `readonly variables`, `defaulted variables` | every name of an `export` / `declare -x` line, not `export -f` / `-n`; every name of a `readonly` / `declare -r` / `typeset -r` / `local -r` line, not `readonly -f`; `${NAME:-default}` and `${NAME:=default}` |
| `script-level assignments` | an assignment at column 0 (`+=` and array elements too), not the environment prefix of a command (`LC_ALL=C sort`) |
| `sourced files`, `traps`, `set -e`, `exit statements with a status` | `source` / `.`, also after `&&`, `;`, `then`, `else`, `do` or `{`; `trap` (not `trap -p`); `set -e` / `-o errexit`; `exit` with a literal status |
| `typeset and declare declarations`, `ksh discipline functions` | `typeset` and `declare`; `function name.get` / `.getn` / `.set` / `.unset` / `.append` |

| `perl-facts` | Counts |
|---|---|
| `[ignore]` POD, `__END__`, heredoc bodies and comment lines | from a `=word` line to `=cut`; from `__END__` or `__DATA__` to the end; `<<"END"`, `<<'END'`, `<< "END"`, `<<\END`, `<<END`, `<<~END` bodies, without an end marker to the end of the file; not a shift after a variable, a number, `)`, `]` or a hash element (`$x <<BITS`, `$h{x} <<BITS`; `print $fh <<END` and `print {$fh} <<END` are heredocs), nor a `<<` in a trailing comment, a string, a quote-like operator or a regex (`q{...}`, `qq/.../`, `m/.../`, `s/.../.../`); whole-line `#` comments; leftmost first |
| `packages`, `subs` | `package` statements and blocks and 5.38 `class` blocks; named, lexical (`my sub`), signature and attribute subs (`sub f :prototype($$) ($x, $y)`) and 5.38 `method`s, not anonymous subs or forward declarations |
| `modules used`, `modules required` | `use Some::Module`, `use parent` / `base` (not other pragmas or versions); `require Module` / `require 'file'` (not versions) |
| `use constant statements`, `package variables (our)`, `file-scope lexicals (my at column 0)` | each `use constant`; `our` declarations; a `my` at column 0 |
| `environment variables read`, `GetOptions calls`, `die, croak and confess` | a read of `$ENV{KEY}`, not an assignment, `local` or `delete`; `GetOptions(...)` and its variants; a call of the three where a statement starts or after `or` / `and` / `\|\|` / `&&` / `//`, so neither `use Carp qw(croak)` nor the German article in a message counts |

| `xml-facts` / `pom-facts` | Counts |
|---|---|
| `[ignore]` comments, CDATA, processing instructions and the DOCTYPE | `<!-- ... -->`, `<![CDATA[ ... ]]>`, `<? ... ?>` and `<!DOCTYPE ... [ ... ]>`, leftmost first (xml-facts); comments, CDATA and the body of every `<plugin>` (pom-facts) |
| `elements`, `namespace declarations`, `id and name attributes` | start tags; `xmlns` / `xmlns:prefix`; `id=` and `name=` attributes inside a tag, also prefixed ones (`android:id`, `xml:id`), not a `name = 'a'` in element text |
| `XSD type definitions`, `XSLT templates`, `Spring beans` | named `complexType` / `simpleType`; `template` elements (not calls); `bean` elements |
| `dependencies`, `managed dependency sections`, `plugins`, `modules`, `profiles`, `test-scoped dependencies`, `managed plugin sections` | pom-facts: each `<dependency>` (managed and profile ones included, not a plugin's own), `<dependencyManagement>`, `<plugin>` (build, management, reporting, profiles; not one inside a plugin configuration), `<module>`, `<profile>`, `<scope>test</scope>`, `<pluginManagement>` |

## Using a profile

- Point `profileDirectory` at it: relative to the directory of the configuration file (CLI and
  plugin alike), or absolute.
- Name keys in the rules. A key that is neither defined in the configuration nor a file of the
  profile fails the run before any model loads, naming the file it looked for.
- To change a prompt or a counter for your own project, copy the directory and point
  `profileDirectory` at the copy.
- A `promptDefinition` or `factDefinition` in the configuration still works and wins over a file of
  the same key: inline (`template`, `facts`) or naming its own file (`templateFile`, `factsFile`,
  relative to `profileDirectory`).

Who reads `v3/`:

- the `srcmorph-selftest` profile of [`srcmorph-maven-plugin/pom.xml`](../srcmorph-maven-plugin/pom.xml),
  through [`srcmorph-selftest.yaml`](../srcmorph-maven-plugin/srcmorph-selftest.yaml)
  (`${ai.profileDir}`, overridable with `-Dai.profileDir=`);
- the model-card benchmark, [`docs/ai-index-benchmark/modelcard/run.py`](../docs/ai-index-benchmark/modelcard/run.py)
  (`--profile v3` by default);
- the examples under [`examples/`](../examples/) (`profileDirectory: "../profiles/v3"`, or
  `"../../profiles/v3"` one level deeper), so the CI smoke of the user-built jar reads it too;
- the plugin's integration-test fixture, [`.github/plugin-it/srcmorph.yaml`](../.github/plugin-it/srcmorph.yaml),
  which also checks that the `java-facts` counts reach the summaries.

## The shared OOP prompt and the enterprise kinds (2026-11-04)

`file-body-oop` replaced `file-body-java`: it keeps the Java prompt's sections and its measured
sentences, and says them for every class-based language (every type of the file, Kotlin's members
without a modifier, top-level and extension functions, coroutines, a specification method named by
a string). Three runs each with both prompts on the model-card corpus gave the same Java recall
(gpt-oss-20b 99.9 to 100 %, GPT-6 Luna 100 %) and quality 99.8 against 99.9 % (gpt-oss) and 99.7
against 100 % (Luna) for `file-body-java` and `file-body-oop`; the language corpus, after the
secrets rule and the XML prompt's new definitions, 98.7 % (gpt-oss, 99.1 % before) and 99.8 % (Luna,
99.5 %). The enterprise corpus ([`corpus-enterprise/`](../docs/ai-index-benchmark/corpus-enterprise/README.md),
written for the benchmark) measured the new kinds once: 97.9 % (gpt-oss) and 97.3 % (Luna);
`web.xml`, the JPA, Hibernate and MyBatis mappings, the Camel routes, the BPMN process, the WSDL,
the JSP, the OpenAPI contract, the Kubernetes manifest and the Dockerfile complete in all six runs,
and no summary copied the password the corpus holds. The runs are under
[`remeasure-2026-11-04/`](../docs/ai-index-benchmark/remeasure-2026-11-04/README.md). Measured are
therefore Java, SQL, shell, Perl, XML and the fallback on the card and language corpora, and the
kinds of the enterprise corpus on that corpus alone; Scala, the contract formats other than OpenAPI,
Terraform, Ansible and Compose ran in no measurement. TypeScript ran once, on a repository measured
internally: with `file-body-oop` both models named more of its exported API than with the fallback,
which is why `run.py` and the Luna example route `.ts`, `.mts` and `.cts` there; it has no counters.
Their counters (`scala-facts`, `proto-facts`,
`avro-facts`, `graphql-facts`, `asyncapi-facts`, `terraform-facts`, `ansible-facts`,
`compose-facts`, `camel-yaml-facts`, and `dmn-facts`, `gradle-facts`, `properties-facts`,
`powershell-facts`, `batch-facts`) are pinned by their tests, not by a model run: a facts line is
written in front of every summary of its rule but reaches the model only in a reduce call, and the
scores read names, not counts.

Three changes followed the same day ([`remeasure-2026-11-04b/`](../docs/ai-index-benchmark/remeasure-2026-11-04b/README.md)):
the user message of a file starts with its path below the base directory, `file-body-oop` names the
private functions, constants and fields the logic uses and each call on a dependency, and
`file-body-ops` names a workflow or pipeline and defines Gradle scripts. The enterprise corpus went
to 99.7 % (gpt-oss) and 99.9 % (Luna), every name the morning's runs left out found in all six runs;
the card corpus gave Luna 100 % again and gpt-oss 99.5 % over six runs, its misses those of earlier
rounds and two runs that named `ringbuf.c`'s headers instead of its functions; nine gpt-oss runs on
the language corpus gave 99.0 %, so the morning's 98.7 % was variation.

## Shell, Perl and XML prompts

Up to these three files, shell scripts, Perl and XML went through `file-body-fallback`. The
benchmark's language corpus ([`corpus-languages/`](../docs/ai-index-benchmark/corpus-languages/):
Perl, Bash, ksh93, POSIX sh, a POM, a Spring context, an XSD, an XSLT) showed what gpt-oss-20b lost
with it: the options and globals of a deploy script (`DRY_RUN`, `HOSTS`, the sourced library),
lower-case ksh variables, a POM's plugins, an XSLT's keys. The prompts ask for those by kind:

- **`file-body-shell`:** the dialect and its strict mode; every argument by the variable that holds
  it and every option by the letter a user types, with its parser; every top-level variable, also
  the lower-case ones, one bullet each as `name=value`; every function, the small helpers too;
  sourced files, external commands and the tables or hosts their heredocs use; exit statuses and
  traps. A name that occurs only inside a heredoc or a comment is not the script's own.
- **`file-body-perl`:** packages and subs, the private `_helper` subs too; `Getopt::Long` options as
  typed; every `%ENV` key; `use constant`, `our` and file-scope `my` configuration; modules used
  and what they import; `die` / `croak` / `confess`. POD and what follows `__END__` are
  documentation.
- **`file-body-xml`:** what the file defines, by kind: a POM's coordinates, dependencies, plugins,
  properties, modules and profiles; a Spring context's beans with class and references; an XSD's
  namespace, elements and types; an XSLT's templates, parameters and keys; otherwise the element
  structure and the repeated records.

The shell prompt took four drafts on gpt-oss: with the first two, it wrote `tmp` without the value it is
built from (the `.partial` file of the benchmark's backup script); with "each as `name=value`" it
copied the assignments into a code fence in two of three runs; "one bullet each" kept both. Three
runs each on OpenRouter, six with the language prompts on the language corpus, scored as the model
card scores (outputs and the command in
[`languages-2026-11/`](../docs/ai-index-benchmark/languages-2026-11/)). The language corpus has only
other files, so its score has two parts, (recall + format share) / 2, against the card's four:

| Corpus | Model | before (`file-body-fallback`) | with the language prompts |
|---|---|---|---|
| language corpus | gpt-oss-20b | 95.7 % (95.3 to 96.1) | 97.7 % (89.5 to 100; 99.1 % without one empty answer) |
| language corpus | GPT-6 Luna | 99.6 % (99.3 to 100) | 99.9 % (99.3 to 100) |
| benchmark | gpt-oss-20b | 100 % in all three | 99.6 % (99.0 to 100) |
| benchmark | GPT-6 Luna | 100 % | 100 % |
| second | gpt-oss-20b | 99.8 % (99.3 to 100) | 99.5 % (98.5 to 100) |
| second | GPT-6 Luna | 100 % | 100 % |

On the language corpus gpt-oss's deploy script went from 70 to 98 %, the POM from 85 to 100 %,
the ksh script from 89 to 97 % (in the five runs that answered it). Runs 4 to 6 came after one
wording change in `file-body-perl` (an option as the user types it, `--max-age`, its spec only as
the source), which left the Perl files where they were. gpt-oss scored 100, 98.5, 99.3, 98.9, 99.7
and 89.5 %: in run 6 the provider returned an empty answer for the ksh script, which srcmorph wrote
as a summary without a body (it logs `AI provider returned empty body`); without that file the run
scores 98.2 % and the six runs 99.1 %.

On the other two corpora every miss with the language prompts is in a Java or SQL file, whose
prompts did not change. One of them recurs: gpt-oss lost `book_free_le_total` of the benchmark's
SQL migration in two of three runs, which v3 kept in all three. Three runs cannot tell that from
variation (Fisher's exact test, p = 0.4), nor show that it is one; the same holds for the reader's
hit@1 of 95 against 92 %, 1.3 of 43 questions. No run wrote a code fence or a `###` heading in a
shell, Perl or XML summary.

The size tiers for big XML files in the plugin's self-test take `file-body-xml` too, each measured
as it runs, three runs each on 2026-11-02 (Granite at temperature 0.2 hardly varies between runs).
`big-xml` (Granite H-Tiny, map-reduce over 64K): on an 870 KB catalog H-Tiny named 7 of 8 expected
names with the XML prompt and 4 with the generic one. `huge-xml` (Granite H-1B, six sampled 16K
chunks): on a 1.15 MB catalog H-1B wrote one correct sentence with the XML prompt, while with the
generic one its final answer ran to the output limit and the file failed, in every run. The runs are
under [`remeasure-2026-11-02/`](../docs/ai-index-benchmark/remeasure-2026-11-02/README.md).
`big-pom` and `huge-pom` keep the generic prompt: no large POM was measured.

## v3 against v2

v2 was written from the misses on the benchmark corpus, so it ran three times on a second corpus as
well, which is kept outside this repository: Java with its SQL migrations, a shell script, C and
Python from other open-source projects. Its gain carried over (gpt-oss-20b 94.6 % with v1, 99.0 %
with v2; GPT-6 Luna 98.6 % and 100 %). The prompts of v3 are v2's with additions for what gpt-oss
still lost there:

- **SQL:** when several `INSERT`s write one table, each statement's count as an addition, then the
  total (the model counted every statement right and summed wrong); every `CREATE EXTENSION` and
  `CREATE TYPE` by name, an enum with its values; every index as its own bullet with its table,
  columns or expressions, method and operator class, also in a file that creates no table.
- **Other files:** the program entry point `main`; for a command-line tool every command,
  subcommand and option by the name a user types, also where the function behind it is named
  differently; a script's arguments by the variable that holds them.
- The Java, package and project prompts and the Java and SQL fact files were unchanged when v3
  was measured; the counters changed afterwards (see "Fact files" above).
- After that measurement (2026-11-01) the prompts changed as well (CHANGELOG, Unreleased): the Java
  family rule names no analysis channel; the SQL, XML and fallback prompts have a family rule for
  long runs of rows, records and constants, and in the SQL prompt more than five `INSERT`s into one
  table are a family; no prompt quotes an example lead any more, and none asks to recount; the
  closing paragraph says that the user message starts with the file name; chunked rules append
  `map-chunk` and `reduce-parts`. The tables below were measured before these changes.

The last two additions came from the drafts in between: with the command-line sentence alone,
gpt-oss described a shell script's arguments as `$1` instead of naming `SOURCE_DIR`, and an
index-only migration lost its index list. Three runs of v3 each, both corpora:

| Corpus | Model | v2 | v3 |
|---|---|---|---|
| benchmark | gpt-oss-20b | 99.0 % (97.0 to 100) | 100 % in all three |
| benchmark | GPT-6 Luna | 100 % | 100 % |
| second | gpt-oss-20b | 99.0 % (98.2 to 100) | 99.8 % (99.3 to 100) |
| second | GPT-6 Luna | 100 % | 100 % |

No format slip, the index did not grow, and the search measurement stayed where it was (model
card, finding 6). gpt-oss's results move by a few points between runs of one unchanged prompt, so
these numbers separate a miss that recurs from one that does not, not a one-point difference.

## v2 against v1

The prompts of v2 are v1's with targeted additions for the names v1 lost in the October 2026
[model card](../docs/ai-index-benchmark/MODEL-CARD.md) (finding 5 there):

- **Java:** the type's own name first in the Type section (gpt-oss-20b left it out of the large
  files), and getters and setters are not a family: every accessor is named, a pair on one short
  line (GPT-6 Luna once named 3 of the 88 accessors of a configuration class).
- **SQL:** every constraint and index by its declared name, also those of `ALTER TABLE`; for data,
  the exact number of rows inserted per table.
- **Other files:** a Constants and configuration section (constants, macros, status codes,
  configuration and environment variables), the internal helper functions, the library functions
  called.
- `package-body` and `project-body` are unchanged.

The examples in the added instructions are neutral names that occur nowhere in the benchmark corpus.
Three runs each on OpenRouter: GPT-6 Luna scored 100 % in all three (v1: 93.6 to 99.0 %), gpt-oss-20b
99.0 % on average (v1: 96.8 %), with no format slip and a barely longer index. The search measurement
did not move beyond its noise (about one of 43 questions lower on average). v2 was written from the
misses on that same corpus, so part of the gain may be fitted to it; a corpus it was not written on
would tell.

The fact counters came with v2 into the profile; before, they were written out in the plugin pom and
the example configuration. Since then `INSERT rows` is `INSERT statements` (it always counted
statements, so a multi-row `INSERT` read as one row), `VALUES rows (approx)` counts the rows, and
materialized views, triggers, types and `ALTER TABLE` are counted.

## A new version, and the earlier ones

The next profile goes into `v4/` with a file for each key, is measured with `run.py --profile v4`,
and replaces `v3/` when it wins; the configurations above then switch to it, and `v3/` goes. Only the
current profile is kept here, the earlier prompt sets are in the history (`git show <commit>:<path>`
prints one; the directory was called `prompts/` then):

| Set | Where | What |
|---|---|---|
| v2 | `profiles/v2/` in commit `b89d028` | the set before v3; the model card's finding 5 measured it |
| v1 | `prompts/v1/` in commit `6fe8335` | the set every run of the October 2026 model card used; before these files existed (commit `cd2e15f`) it was inline in the plugin pom and in the benchmark's `prompts.json`, where the pom's CDATA added a leading line break and a trailing line break plus 36 spaces |
| the June 2026 experiment, then also called v2 | `prompts/v2/` in commit `cd2e15f` | trivial-file omission, JavaBean accessor grouping, a "no invented keywords" ban, single-child package pass-through, more brevity; not adopted, because code-specialized models started wrapping sections in code fences ([COMPARISON.md](../docs/ai-index-benchmark/COMPARISON.md) "Prompt v1 vs v2") |
| v1 as the June 2026 benchmark ran it | `prompts/archive/2026-06/v1/` in commit `cd2e15f` | before the system/user split (`4743e77`), with the `File: %s` / `Source: %s` placeholders at its end |
