<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: MIT OR Apache-2.0
-->

# Third-party license and attribution notes

The project is Apache-2.0. Logback in the concrete core dependency declaration is
test-scoped; its parent dependencyManagement pin is not a runtime dependency.
srcmorph publishes no runnable jar. The jar a user builds from `examples/build-your-own-jar`
bundles SLF4J Simple and the llama natives they select; those native artifacts have additional
third-party contents requiring their own review, and redistributing such a jar is the builder's
responsibility.

## Review evidence

See [the dependency license review](https://github.com/bernardladenthin/actions/blob/main/docs/dependency-license-review.md) of bernardladenthin/actions
for exact-version evidence and remaining decisions. This inventory is not an approval of all
licenses found by a scanner. Test-tool redistribution requires a separate review.

For unchanged Maven components, source is available from
`https://repo.maven.apache.org/maven2/<groupId with slashes>/<artifactId>/<version>/`
as `<artifactId>-<version>-sources.jar`; POMs and upstream source repositories provide provenance.
Preserve copyright, license and NOTICE files when redistributing dependencies.

Up to 1.3.0 srcmorph also released fat jars of the CLI, with the original dependency legal files
collected under `META-INF/third-party/dependencies/`. From 1.4.0 there are none, and that collection
step is gone with them.
This source-location statement does not clear unknown proprietary or historical Git findings.
