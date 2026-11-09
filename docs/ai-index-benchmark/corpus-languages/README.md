# Language corpus

Nine source files in the languages the model-card corpus covers least: Perl, Bash, ksh93, POSIX sh
and XML. They are written for this benchmark (Apache-2.0, like the rest of the repository), so they
can be shipped and changed freely, and each one holds the traps the fact counters and the prompts
have to get past: a sub named in POD and in a usage heredoc, a `set -e` that belongs to a remote
shell, Oracle SQL inside a ksh heredoc, a commented-out XML element.

| File | What it is |
|---|---|
| [`other/perl/LogDigest.pm`](other/perl/LogDigest.pm) | a module: per-host and per-status counts of an access log, constants, POD after `__END__` |
| [`other/perl/log_digest.pl`](other/perl/log_digest.pl) | its command-line script: Getopt::Long options, `%ENV`, a usage heredoc |
| [`other/shell/deploy_release.sh`](other/shell/deploy_release.sh) | Bash: rolls a release out host by host, getopts, arrays, a remote heredoc, a trap |
| [`other/shell/nightly_extract.ksh`](other/shell/nightly_extract.ksh) | ksh93: a nightly Oracle export with `typeset`, `function name {` and a `<<-` SQL heredoc |
| [`other/shell/healthcheck.sh`](other/shell/healthcheck.sh) | POSIX sh: an init-style start / stop / status / health wrapper |
| [`other/xml/invoice-service-pom.xml`](other/xml/invoice-service-pom.xml) | a Maven POM (named so that dependency scanners do not take it for a real module) |
| [`other/xml/scheduler-context.xml`](other/xml/scheduler-context.xml) | a Spring context: beans, placeholders, component scan, a scheduled task |
| [`other/xml/shipment.xsd`](other/xml/shipment.xsd) | an XML Schema: named types, an enumeration, a restriction |
| [`other/xml/statement-to-html.xsl`](other/xml/statement-to-html.xsl) | an XSLT stylesheet: matched and named templates, parameters, a key |

What a faithful summary names is in [`../modelcard/facts-languages.json`](../modelcard/facts-languages.json);
the exact counts the fact files give for each file are pinned by `ShellFactPatternsTest`,
`PerlFactPatternsTest` and `XmlFactPatternsTest` (`srcmorph` module). To measure a profile on it:

```bash
python run.py --cp <classpath file> --models gpt-oss-20b,gpt-6-luna --work <dir> \
    --corpus ../corpus-languages --out <dir> --meta <dir> [--facts]
python score.py --work <dir> --out <dir> --meta <dir> --scores <dir> --facts facts-languages.json
```
