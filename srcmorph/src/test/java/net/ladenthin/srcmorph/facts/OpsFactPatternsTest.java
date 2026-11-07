// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the fact counters of the profile for API contracts and operations files
 * ({@code profiles/v3/openapi-facts.tsv} for the prompt {@code file-body-contract};
 * {@code k8s-facts.tsv}, {@code actions-facts.tsv} and {@code dockerfile-facts.tsv} for
 * {@code file-body-ops}), counted the way the facts line counts them: comments, and in an OpenAPI
 * document the body of a block scalar, are blanked first, so a path or a kind quoted in a description
 * does not count.
 */
public class OpsFactPatternsTest {

    private static final List<FactCounter> OPENAPI = ProfileFactPatterns.counters("openapi-facts");

    private static final List<FactCounter> K8S = ProfileFactPatterns.counters("k8s-facts");

    private static final List<FactCounter> ACTIONS = ProfileFactPatterns.counters("actions-facts");

    private static final List<FactCounter> DOCKERFILE = ProfileFactPatterns.counters("dockerfile-facts");

    private static final List<FactCounter> ASYNCAPI = ProfileFactPatterns.counters("asyncapi-facts");

    private static final List<FactCounter> TERRAFORM = ProfileFactPatterns.counters("terraform-facts");

    private static final List<FactCounter> ANSIBLE = ProfileFactPatterns.counters("ansible-facts");

    private static final List<FactCounter> COMPOSE = ProfileFactPatterns.counters("compose-facts");

    private static final List<FactCounter> CAMEL_YAML = ProfileFactPatterns.counters("camel-yaml-facts");

    private static final List<FactCounter> GRADLE = ProfileFactPatterns.counters("gradle-facts");

    private static final List<FactCounter> PROPERTIES = ProfileFactPatterns.counters("properties-facts");

    private static final List<FactCounter> POWERSHELL = ProfileFactPatterns.counters("powershell-facts");

    private static final List<FactCounter> BATCH = ProfileFactPatterns.counters("batch-facts");

    private static final List<FactCounter> NPM = ProfileFactPatterns.counters("npm-facts");

    private static int count(final List<FactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="openapi-facts">
    private static final String OPENAPI_YAML = "openapi: 3.0.3\n"
            + "info:\n"
            + "  title: Orders\n"
            + "  description: |\n"
            + "    Lists orders. Example path:\n"
            + "    /fake/path:\n"
            + "      get:\n"
            + "        type: object\n"
            + "  version: 1.0.0\n"
            + "# /commented:\n"
            + "paths:\n"
            + "  /orders:\n"
            + "    get:\n"
            + "      operationId: listOrders\n"
            + "      parameters:\n"
            + "        - name: limit\n"
            + "          in: query\n"
            + "      responses:\n"
            + "        '200':\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: '#/components/schemas/Order'\n"
            + "    post:\n"
            + "      operationId: createOrder\n"
            + "      deprecated: true\n"
            + "  /orders/{id}:\n"
            + "    get:\n"
            + "      operationId: getOrder\n"
            + "      parameters:\n"
            + "        - in: path\n"
            + "          name: id\n"
            + "components:\n"
            + "  schemas:\n"
            + "    Order:\n"
            + "      type: object\n"
            + "  securitySchemes:\n"
            + "    bearer:\n"
            + "      type: http\n";

    @Test
    public void openapi_yaml_countsPathsOperationsAndSchemasAndNotADescription() {
        // act, assert
        assertThat(count(OPENAPI, "paths", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "operations (approx)", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "operationIds", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "parameters (approx)", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "$ref references", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "object schemas", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "security schemes", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "deprecated operations and fields", OPENAPI_YAML), is(1));
    }

    @Test
    public void openapi_minifiedJson_countsTheSameKeys() {
        // arrange
        final String json = "{\"openapi\":\"3.0.3\",\"paths\":{\"/orders\":{\"get\":{\"operationId\":\"listOrders\","
                + "\"parameters\":[{\"in\":\"query\",\"name\":\"limit\"}]},\"post\":{\"operationId\":\"createOrder\"}}},"
                + "\"components\":{\"schemas\":{\"Order\":{\"type\":\"object\"}}}}";

        // act, assert
        assertThat(count(OPENAPI, "paths", json), is(1));
        assertThat(count(OPENAPI, "operations (approx)", json), is(2));
        assertThat(count(OPENAPI, "operationIds", json), is(2));
        assertThat(count(OPENAPI, "parameters (approx)", json), is(1));
        assertThat(count(OPENAPI, "object schemas", json), is(1));
    }

    @Test
    public void openapi_minifiedJsonWithAHashInAString_blanksNoKeyAfterIt() {
        // arrange: a " #" inside a JSON string is no YAML comment, and the whole spec is one line
        final String json = "{\"openapi\":\"3.0.3\",\"paths\":{\"/issues\":{\"get\":{\"operationId\":\"list\","
                + "\"description\":\"Fixes issue #12\"},\"post\":{\"operationId\":\"create\"}},"
                + "\"/x\":{\"get\":{\"operationId\":\"x\",\"responses\":{\"200\":{\"$ref\":\"#/components/r\"}}}}}}";

        // act, assert
        assertThat(count(OPENAPI, "paths", json), is(2));
        assertThat(count(OPENAPI, "operations (approx)", json), is(3));
        assertThat(count(OPENAPI, "operationIds", json), is(3));
        assertThat(count(OPENAPI, "$ref references", json), is(1));
    }

    @Test
    public void openapi_hashInADoubleQuotedYamlString_isNoComment() {
        // arrange
        final String yaml = "paths:\n"
                + "  /issues:\n"
                + "    get: {summary: \"Fixes #12\", operationId: list}\n"
                + "    post:\n"
                + "      summary: \"see #3\" # operationId: commented\n"
                + "      operationId: create\n";

        // act, assert
        assertThat(count(OPENAPI, "operations (approx)", yaml), is(2));
        assertThat(count(OPENAPI, "operationIds", yaml), is(2));
    }

    @Test
    public void openapi_emptyBlockScalarOfAListItem_blanksNoSiblingKey() {
        // arrange: the empty scalar's next line is a sibling of description, as deep as its key
        final String yaml = "paths:\n"
                + "  /a:\n"
                + "    get:\n"
                + "      parameters:\n"
                + "        - description: >\n"
                + "          in: query\n"
                + "          name: q\n"
                + "        - description: |\n"
                + "            in: path\n"
                + "          in: header\n";

        // act, assert
        // query and header; not the "in: path" of the second description
        assertThat(count(OPENAPI, "parameters (approx)", yaml), is(2));
    }

    /** The accepted approximation: an apiKey scheme's in: has a parameter's shape, hence "(approx)". */
    @Test
    public void openapi_apiKeySecurityScheme_countsItsInWithTheParameters() {
        // arrange
        final String yaml = "components:\n"
                + "  parameters:\n"
                + "    Trace:\n"
                + "      name: X-Trace\n"
                + "      in: header\n"
                + "  securitySchemes:\n"
                + "    key:\n"
                + "      type: apiKey\n"
                + "      name: X-Key\n"
                + "      in: header\n";

        // act, assert
        assertThat(count(OPENAPI, "parameters (approx)", yaml), is(2));
        assertThat(count(OPENAPI, "security schemes", yaml), is(1));
    }

    @Test
    public void openapi_schemaPropertiesNamedLikeMethods_areNoOperations() {
        // arrange
        final String yaml = "paths:\n"
                + "  /status:\n"
                + "    parameters:\n"
                + "      - name: verbose\n"
                + "        in: query\n"
                + "    get:\n"
                + "      operationId: status\n"
                + "      responses:\n"
                + "        '200':\n"
                + "          content:\n"
                + "            application/json:\n"
                + "              schema:\n"
                + "                type: object\n"
                + "                properties:\n"
                + "                  options:\n"
                + "                    type: string\n"
                + "                  head:\n"
                + "                    type: string\n"
                + "components:\n"
                + "  schemas:\n"
                + "    Probe:\n"
                + "      properties:\n"
                + "        get:\n"
                + "          type: object\n"
                + "        trace:\n"
                + "          type: string\n";

        // act, assert
        // the get of /status; not the properties options, head, get and trace
        assertThat(count(OPENAPI, "operations (approx)", yaml), is(1));
    }

    @Test
    public void openapi_pathItemKeysBeforeAndBetweenTheOperations_countEachOperation() {
        // arrange
        final String yaml = "paths:\n"
                + "  '/orders/{id}':\n"
                + "    summary: One order\n"
                + "    parameters:\n"
                + "    - name: id\n"
                + "      in: path\n"
                + "    get:\n"
                + "      operationId: get\n"
                + "\n"
                + "    x-internal: true\n"
                + "    'delete':\n"
                + "      operationId: remove\n"
                + "  /health:\n"
                + "    $ref: './health.yaml'\n"
                + "  /ping:\n"
                + "    head: {responses: {}}\n"
                + "    options: &probe\n"
                + "      responses: {}\n";

        // act, assert
        // get and delete of /orders/{id}, head and options of /ping; /health has none of its own
        assertThat(count(OPENAPI, "operations (approx)", yaml), is(4));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="k8s-facts">
    private static final String MANIFEST = "# kind: Commented\n"
            + "apiVersion: apps/v1\n"
            + "kind: Deployment\n"
            + "metadata:\n"
            + "  name: orders\n"
            + "spec:\n"
            + "  replicas: {{ .Values.replicas }}\n"
            + "  template:\n"
            + "    spec:\n"
            + "      containers:\n"
            + "        - name: app\n"
            + "          image: shop/orders:1.4.0\n"
            + "          ports:\n"
            + "            - containerPort: 8080\n"
            + "          livenessProbe:\n"
            + "            httpGet: {path: /health}\n"
            + "          env:\n"
            + "            - name: DB_PASSWORD\n"
            + "              valueFrom:\n"
            + "                secretKeyRef:\n"
            + "                  name: db\n"
            + "{{- /* if .Values.debug */ -}}\n"
            + "{{- if .Values.ingress.enabled }}\n"
            + "---\n"
            + "apiVersion: v1\n"
            + "kind: Service\n"
            + "metadata:\n"
            + "  name: orders\n"
            + "---\n"
            + "kind: ConfigMap\n"
            + "data:\n"
            + "  manifest.yaml: |\n"
            + "    kind: Secret\n"
            + "{{- end }}\n";

    @Test
    public void k8s_manifests_countsResourcesAtColumnZeroAndNotACommentOrAQuotedManifest() {
        // act, assert
        assertThat(count(K8S, "resources", MANIFEST), is(3));
        assertThat(count(K8S, "workloads", MANIFEST), is(1));
        assertThat(count(K8S, "Services, Ingresses and Routes", MANIFEST), is(1));
        assertThat(count(K8S, "ConfigMaps and Secrets", MANIFEST), is(1));
    }

    @Test
    public void k8s_manifestsAndHelmTemplates_countsContainersAndHelmActionsAndNotAHelmComment() {
        // act, assert
        assertThat(count(K8S, "container images", MANIFEST), is(1));
        assertThat(count(K8S, "container ports", MANIFEST), is(1));
        assertThat(count(K8S, "probes", MANIFEST), is(1));
        assertThat(count(K8S, "secret references", MANIFEST), is(1));
        assertThat(count(K8S, "Helm control actions (approx)", MANIFEST), is(1));
        assertThat(count(K8S, ".Values references", MANIFEST), is(2));
    }

    @Test
    public void k8s_manifestEmbeddedInAConfigMap_countsNoContainerButItsHelmValues() {
        // arrange
        final String configMap = "apiVersion: v1\n"
                + "kind: ConfigMap\n"
                + "data:\n"
                + "  pod.yaml: |\n"
                + "    kind: Pod\n"
                + "    spec:\n"
                + "      containers:\n"
                + "        - image: busybox\n"
                + "          ports:\n"
                + "            - containerPort: 80\n"
                + "          livenessProbe: {}\n"
                + "  app.properties: |\n"
                + "    url={{ .Values.url }}\n"
                + "    {{- if .Values.debug }}debug=true{{- end }}\n"
                + "  # url={{ .Values.old }}\n";

        // act, assert
        assertThat(count(K8S, "container images", configMap), is(0));
        assertThat(count(K8S, "container ports", configMap), is(0));
        assertThat(count(K8S, "probes", configMap), is(0));
        // Helm renders a block scalar too: url and debug; not the commented-out one
        assertThat(count(K8S, ".Values references", configMap), is(2));
        assertThat(count(K8S, "Helm control actions (approx)", configMap), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actions-facts">
    private static final String WORKFLOW = "name: CI\n"
            + "on:\n"
            + "  push:\n"
            + "  schedule:\n"
            + "    - cron: '0 3 * * *'\n"
            + "permissions:\n"
            + "  contents: read\n"
            + "jobs:\n"
            + "  build:\n"
            + "    runs-on: ubuntu-latest\n"
            + "    strategy:\n"
            + "      matrix:\n"
            + "        java: [21, 25]\n"
            + "    steps:\n"
            + "      - uses: actions/checkout@v4\n"
            + "      - name: Build\n"
            + "        run: mvn -B verify\n"
            + "        env:\n"
            + "          TOKEN: ${{ secrets.SONAR_TOKEN }}\n"
            + "      # - uses: old/action@v1\n"
            + "  release:\n"
            + "    uses: ./.github/workflows/release.yml\n"
            + "    secrets: inherit\n";

    @Test
    public void actions_workflow_countsJobsStepsActionsAndSecretNames() {
        // act, assert
        assertThat(count(ACTIONS, "jobs on a runner", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "reusable workflow calls", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "steps (approx)", WORKFLOW), is(2));
        assertThat(count(ACTIONS, "actions used", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "secret references", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "cron schedules", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "matrices", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "permissions blocks", WORKFLOW), is(1));
    }

    @Test
    public void actions_reusableWorkflowOfAnotherRepository_countsAsACallAndNotAsAnAction() {
        // arrange
        final String workflow = "jobs:\n"
                + "  deploy:\n"
                + "    uses: shop/shared/.github/workflows/deploy.yml@v1\n"
                + "  lint:\n"
                + "    uses: \"shop/shared/.github/workflows/lint.yml@main\"\n"
                + "  build:\n"
                + "    runs-on: ubuntu-latest\n"
                + "    steps:\n"
                + "      - uses: actions/checkout@v4\n"
                + "      - uses: github/codeql-action/init@v3\n";

        // act, assert
        assertThat(count(ACTIONS, "reusable workflow calls", workflow), is(2));
        // actions/checkout and the action in a path of a repository, github/codeql-action/init
        assertThat(count(ACTIONS, "actions used", workflow), is(2));
    }

    @Test
    public void actions_secretsAnywhereInAnExpression_countEach() {
        // arrange
        final String workflow = "    env:\n"
                + "      A: ${{ secrets.A }}\n"
                + "      B: ${{ inputs.token || secrets.B }}\n"
                + "      C: ${{ secrets['C-KEY'] }}\n"
                + "      D: ${{ secrets.D && secrets.E }}\n"
                + "    secrets: inherit\n"
                + "    run: cat secrets.txt\n";

        // act, assert
        // A, B, C-KEY, D and E; not the secrets: key nor a file named secrets.txt outside an expression
        assertThat(count(ACTIONS, "secret references", workflow), is(5));
    }

    @Test
    public void actions_matrixIncludeAndExcludeEntries_areNoSteps() {
        // arrange
        final String workflow = "jobs:\n"
                + "  build:\n"
                + "    runs-on: ${{ matrix.os }}\n"
                + "    strategy:\n"
                + "      matrix:\n"
                + "        include:\n"
                + "          - name: linux\n"
                + "            os: ubuntu-latest\n"
                + "          - name: windows\n"
                + "            os: windows-latest\n"
                + "        exclude:\n"
                + "        - name: mac\n"
                + "    steps:\n"
                + "      - name: Build\n"
                + "        run: mvn verify\n"
                + "      - uses: actions/checkout@v4\n";

        // act, assert
        assertThat(count(ACTIONS, "steps (approx)", workflow), is(2));
        assertThat(count(ACTIONS, "matrices", workflow), is(1));
        assertThat(count(ACTIONS, "jobs on a runner", workflow), is(1));
    }

    @Test
    public void actions_inputsAndOutputsNamedLikeJobKeys_areNoRunnersNorMatrices() {
        // arrange
        final String workflow = "on:\n"
                + "  workflow_call:\n"
                + "    inputs:\n"
                + "      runs-on:\n"
                + "        description: The runner\n"
                + "        type: string\n"
                + "        default: ubuntu-latest\n"
                + "    outputs:\n"
                + "      matrix:\n"
                + "        description: The matrix\n"
                + "        value: ${{ jobs.setup.outputs.matrix }}\n"
                + "jobs:\n"
                + "  setup:\n"
                + "    runs-on: ${{ inputs.runs-on }}\n"
                + "    outputs:\n"
                + "      matrix: ${{ steps.set.outputs.matrix }}\n"
                + "    steps:\n"
                + "      - id: set\n"
                + "        run: echo \"matrix=[1,2]\" >> \"$GITHUB_OUTPUT\"\n"
                + "  build:\n"
                + "    needs: setup\n"
                + "    runs-on: ubuntu-latest\n"
                + "    strategy:\n"
                + "      matrix: ${{ fromJSON(needs.setup.outputs.matrix) }}\n"
                + "    steps:\n"
                + "      - run: echo ${{ matrix.n }}\n";
        final String action = "inputs:\n  runs-on:\n    description: x\nruns:\n  using: composite\n";

        // act, assert
        // setup and build; not the input runs-on
        assertThat(count(ACTIONS, "jobs on a runner", workflow), is(2));
        // the strategy's; not the outputs named matrix
        assertThat(count(ACTIONS, "matrices", workflow), is(1));
        assertThat(count(ACTIONS, "steps (approx)", workflow), is(2));
        assertThat(count(ACTIONS, "jobs on a runner", action), is(0));
    }

    @Test
    public void actions_cronEntriesInFlowStyle_count() {
        // arrange
        final String inline = "on:\n  schedule: [{cron: \"0 3 * * *\"}, { \"cron\": '0 4 * * 1' }]\n";
        final String items = "on:\n  schedule:\n    - {cron: '30 1 * * *'}\n    - cron: '0 2 * * *'\n";

        // act, assert
        assertThat(count(ACTIONS, "cron schedules", inline), is(2));
        assertThat(count(ACTIONS, "cron schedules", items), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dockerfile-facts">
    private static final String DOCKERFILE_SOURCE = "# syntax=docker/dockerfile:1\n"
            + "FROM maven:3.9-eclipse-temurin-21 AS build\n"
            + "ARG VERSION=1.0\n"
            + "COPY pom.xml .\n"
            + "RUN mvn -B package \\\n"
            + "    && echo FROM done\n"
            + "from eclipse-temurin:21-jre\n"
            + "ENV JAVA_OPTS=-Xmx512m\n"
            + "COPY --from=build /app/target/app.jar /app.jar\n"
            + "EXPOSE 8080\n"
            + "USER app\n"
            + "HEALTHCHECK CMD curl -f http://localhost:8080/health\n"
            + "# RUN rm -rf /\n"
            + "ENTRYPOINT [\"java\", \"-jar\", \"/app.jar\"]\n";

    @Test
    public void dockerfile_instructionsInAnyCase_countsInstructionsAtTheStartOfALine() {
        // act, assert
        assertThat(count(DOCKERFILE, "build stages", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "RUN instructions", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "environment entries", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "exposed ports", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "users", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "entrypoints and commands", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "healthchecks", DOCKERFILE_SOURCE), is(1));
    }

    @Test
    public void dockerfile_linesABackslashContinues_doNotCountAsInstructions() {
        // arrange
        final String dockerfile = "FROM alpine\n"
                + "RUN set -e; \\\n"
                + "env LANG=C sort list \\\n"
                + "# a comment inside the command\n"
                + "\n"
                + "copy src dst \\  \n"
                + "    && from-cache\n"
                + "ENV A=1\n"
                + "RUN cmd /c C:\\dir\\ x\n"
                + "arg B\n";

        // act, assert
        assertThat(count(DOCKERFILE, "environment entries", dockerfile), is(2));
        assertThat(count(DOCKERFILE, "RUN instructions", dockerfile), is(2));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", dockerfile), is(0));
        assertThat(count(DOCKERFILE, "build stages", dockerfile), is(1));
    }

    @Test
    public void dockerfile_continuedLinesWithWindowsLineBreaks_doNotCountAsInstructions() {
        // act, assert
        assertThat(count(DOCKERFILE, "environment entries", "RUN a \\\r\nenv X=1\r\nENV Y=2\r\n"), is(1));
    }

    @Test
    public void dockerfile_buildKitHeredocs_doNotCountTheirBodies() {
        // arrange
        final String dockerfile = "# syntax=docker/dockerfile:1\n"
                + "FROM alpine\n"
                + "RUN <<EOF\n"
                + "apk add curl\n"
                + "FROM inside\n"
                + "ENV X=1\n"
                + "EOF\n"
                + "COPY <<-\"CONF\" /etc/app.conf\n"
                + "\tUSER app\n"
                + "\tRUN not-an-instruction\n"
                + "\tCONF\n"
                + "USER app\n"
                + "RUN echo $((1 << 2))\n";

        // act, assert
        assertThat(count(DOCKERFILE, "build stages", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "RUN instructions", dockerfile), is(2));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "environment entries", dockerfile), is(0));
        assertThat(count(DOCKERFILE, "users", dockerfile), is(1));
    }

    @Test
    public void dockerfile_unclosedHeredoc_runsToTheEndOfTheFile() {
        // act, assert
        assertThat(count(DOCKERFILE, "RUN instructions", "FROM alpine\nRUN <<EOF\necho\nRUN x\nEOFX\n"), is(1));
        assertThat(count(DOCKERFILE, "RUN instructions", "RUN <<EOF\r\necho\r\nEOF\r\nRUN b\r\n"), is(2));
    }

    @Test
    public void dockerfile_shiftInsideAWordOfRun_opensNoHeredoc() {
        // arrange
        final String dockerfile = "FROM alpine\nRUN python3 -c \"print(1<<x)\"\nRUN apk add curl\nCOPY a /b\nENV A=1\n";

        // act, assert
        assertThat(count(DOCKERFILE, "RUN instructions", dockerfile), is(2));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "environment entries", dockerfile), is(1));
    }

    @Test
    public void dockerfile_lessThanLessThanInALabelOrEnvValue_opensNoHeredoc() {
        // arrange
        final String label = "FROM alpine\nLABEL note=\"a <<b\"\nRUN apk add curl\nCOPY a /b\n";
        final String env = "FROM alpine\nENV NOTE=x <<b\nRUN apk add curl\nCOPY a /b\n";

        // act, assert
        assertThat(count(DOCKERFILE, "RUN instructions", label), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", label), is(1));
        assertThat(count(DOCKERFILE, "RUN instructions", env), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", env), is(1));
    }

    @Test
    public void dockerfile_hereString_opensNoHeredoc() {
        // act, assert
        assertThat(
                count(DOCKERFILE, "RUN instructions", "FROM alpine\nRUN cat <<<word\nUSER app\nRUN apk add x\n"),
                is(2));
    }

    @Test
    public void dockerfile_heredocOnALineARunContinues_doesNotCountItsBody() {
        // arrange
        final String indented =
                "FROM alpine\nRUN apt-get update \\\n && cat <<EOF > /etc/x\nFROM evil\nUSER root\nEOF\nUSER app\n";
        final String atTheLineStart = "FROM a\nRUN echo \\\n<<EOF\nFROM inner\nEOF\nUSER app\n";

        // act, assert
        assertThat(count(DOCKERFILE, "build stages", indented), is(1));
        assertThat(count(DOCKERFILE, "users", indented), is(1));
        assertThat(count(DOCKERFILE, "build stages", atTheLineStart), is(1));
        assertThat(count(DOCKERFILE, "users", atTheLineStart), is(1));
    }

    /** Docker's documented form: two heredocs on one line, their bodies one after the other. */
    @Test
    public void dockerfile_twoHeredocsOnOneLine_doNotCountEitherBody() {
        // arrange
        final String dockerfile = "FROM alpine\n"
                + "COPY <<robots.txt <<humans.txt /dst/\n"
                + "User-agent: *\n"
                + "robots.txt\n"
                + "FROM inside\n"
                + "EXPOSE 1\n"
                + "humans.txt\n"
                + "EXPOSE 80\n"
                + "RUN a \\\n"
                + " && cat <<x <<'y' > /f\n"
                + "FROM in1\n"
                + "x\n"
                + "FROM in2\n"
                + "y\n"
                + "USER app\n";

        // act, assert
        assertThat(count(DOCKERFILE, "build stages", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "exposed ports", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "RUN instructions", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "users", dockerfile), is(1));
    }

    /** A << inside quotes opens no heredoc that would never close and blank the rest of the file. */
    @Test
    public void dockerfile_lessThanLessThanInsideQuotes_opensNoHeredoc() {
        // arrange
        final String dockerfile = "FROM a\n"
                + "RUN echo \"usage: cmd <<file>\" > /usage.txt\n"
                + "RUN python3 <<'PY'\n"
                + "print(\"FROM x\")\n"
                + "PY\n"
                + "FROM b\n"
                + "RUN apk add curl\n"
                + "RUN echo 'a <<b' && cat <<EOF > \"/it's here\"\n"
                + "USER inside\n"
                + "EOF\n"
                + "USER app\n";

        // act, assert
        assertThat(count(DOCKERFILE, "build stages", dockerfile), is(2));
        assertThat(count(DOCKERFILE, "RUN instructions", dockerfile), is(4));
        assertThat(count(DOCKERFILE, "users", dockerfile), is(1));
    }

    @Test
    public void dockerfile_lessThanLessThanInsideQuotesOnAContinuedLine_opensNoHeredoc() {
        // arrange
        final String dockerfile = "FROM a\nRUN set -e \\\n && echo \"a <<b\" \\\nenv X=1 \\\nfrom x\nUSER app\n";

        // act, assert
        assertThat(count(DOCKERFILE, "environment entries", dockerfile), is(0));
        assertThat(count(DOCKERFILE, "build stages", dockerfile), is(1));
        assertThat(count(DOCKERFILE, "users", dockerfile), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="asyncapi-facts">
    private static final String ASYNCAPI_V2 = "asyncapi: 2.6.0\n"
            + "info:\n"
            + "  title: Orders\n"
            + "  description: |\n"
            + "    Publishes orders.\n"
            + "    publish:\n"
            + "servers:\n"
            + "  prod:\n"
            + "    url: broker:9092\n"
            + "    protocol: kafka\n"
            + "channels:\n"
            + "  order/placed:\n"
            + "    publish:\n"
            + "      operationId: placeOrder\n"
            + "      bindings:\n"
            + "        kafka:\n"
            + "          clientId: shop\n"
            + "      message:\n"
            + "        correlationId:\n"
            + "          location: $message.header#/id\n"
            + "        payload:\n"
            + "          type: object\n"
            + "  # order/old:\n"
            + "  order/shipped:\n"
            + "    subscribe:\n"
            + "      message:\n"
            + "        $ref: '#/components/messages/Shipped'\n"
            + "components:\n"
            + "  securitySchemes:\n"
            + "    sasl:\n"
            + "      type: scramSha512\n";

    @Test
    public void asyncapi_versionTwoDocument_countsOperationsAndNotTheDescription() {
        // act, assert
        assertThat(count(ASYNCAPI, "operations", ASYNCAPI_V2), is(2));
        assertThat(count(ASYNCAPI, "operationIds", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "payloads", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "server protocols", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "bindings", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "correlation ids", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "$ref references", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "object schemas", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "security schemes", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "channel addresses (3.x)", ASYNCAPI_V2), is(0));
    }

    @Test
    public void asyncapi_aSchemaPropertyNamedAddress_isNoChannelAddress() {
        // arrange
        final String schema = "asyncapi: 2.6.0\n"
                + "components:\n"
                + "  schemas:\n"
                + "    Customer:\n"
                + "      properties:\n"
                + "        address:\n"
                + "          type: string\n"
                + "        billing: {\"address\": {\"type\": \"string\"}}\n";

        // act, assert
        assertThat(count(ASYNCAPI, "channel addresses (3.x)", schema), is(0));
        assertThat(
                count(ASYNCAPI, "channel addresses (3.x)", "{\"channels\": {\"a\": {\"address\": \"order/a\"}}}"),
                is(1));
    }

    @Test
    public void asyncapi_versionThreeDocument_countsChannelsAndActions() {
        // arrange
        final String v3 = "asyncapi: 3.0.0\n"
                + "channels:\n"
                + "  placed:\n"
                + "    address: order/placed\n"
                + "  shipped:\n"
                + "    address: 'order/{id}/shipped'\n"
                + "operations:\n"
                + "  onPlaced:\n"
                + "    action: receive\n"
                + "  sendShipped:\n"
                + "    action: send\n"
                + "    # action: send\n";

        // act, assert
        assertThat(count(ASYNCAPI, "channel addresses (3.x)", v3), is(2));
        assertThat(count(ASYNCAPI, "operations", v3), is(2));
    }

    @Test
    public void asyncapi_payloadsOfMessageExamples_areNoPayloads() {
        // arrange
        final String yaml = "asyncapi: 2.6.0\n"
                + "channels:\n"
                + "  orders:\n"
                + "    publish:\n"
                + "      message:\n"
                + "        payload:\n"
                + "          type: object\n"
                + "        examples:\n"
                + "          - name: placed\n"
                + "            payload:\n"
                + "              id: 1\n"
                + "          - payload:\n"
                + "              id: 2\n"
                + "        correlationId:\n"
                + "          location: $message.header#/id\n";

        // act, assert
        assertThat(count(ASYNCAPI, "payloads", yaml), is(1));
        assertThat(count(ASYNCAPI, "correlation ids", yaml), is(1));
    }

    @Test
    public void asyncapi_minifiedJsonWithAHashInAString_blanksNoKeyAfterIt() {
        // arrange
        final String json = "{\"asyncapi\":\"2.6.0\",\"channels\":{\"chat\":{\"publish\":{\"operationId\":\"post\","
                + "\"description\":\"Posts to the #general channel\",\"message\":{\"payload\":{\"type\":\"object\"}}}}}}";

        // act, assert
        assertThat(count(ASYNCAPI, "operations", json), is(1));
        assertThat(count(ASYNCAPI, "operationIds", json), is(1));
        assertThat(count(ASYNCAPI, "payloads", json), is(1));
        assertThat(count(ASYNCAPI, "object schemas", json), is(1));
    }

    @Test
    public void asyncapi_emptyBlockScalar_blanksNoSiblingKey() {
        // arrange
        final String yaml = "asyncapi: 2.6.0\n"
                + "channels:\n"
                + "  orders:\n"
                + "    description: >\n"
                + "    publish:\n"
                + "      operationId: placeOrder\n";

        // act, assert
        assertThat(count(ASYNCAPI, "operations", yaml), is(1));
        assertThat(count(ASYNCAPI, "operationIds", yaml), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="terraform-facts">
    private static final String TERRAFORM_SOURCE = "terraform {\n"
            + "  required_providers {\n"
            + "    aws = { source = \"hashicorp/aws\" }\n"
            + "  }\n"
            + "  backend \"s3\" {\n"
            + "    bucket = \"state\"\n"
            + "  }\n"
            + "}\n"
            + "provider \"aws\" {\n"
            + "  region = var.region\n"
            + "}\n"
            + "variable \"region\" {\n"
            + "  default = \"eu-central-1\"\n"
            + "}\n"
            + "variable \"db_password\" {\n"
            + "  sensitive = true\n"
            + "}\n"
            + "locals {\n"
            + "  name = \"shop # not a comment\"\n"
            + "}\n"
            + "# resource \"aws_s3_bucket\" \"old\" {}\n"
            + "// module \"old\" {}\n"
            + "/* data \"aws_ami\" \"old\" {} */\n"
            + "data \"aws_ami\" \"base\" {\n"
            + "  most_recent = true\n"
            + "}\n"
            + "resource \"aws_instance\" \"web\" {\n"
            + "  count    = 2\n"
            + "  provider = aws.west\n"
            + "  user_data = <<-EOF\n"
            + "    resource \"fake\" \"x\" {}\n"
            + "    EOF\n"
            + "  dynamic \"ebs_block_device\" {\n"
            + "    for_each = var.disks\n"
            + "    content {}\n"
            + "  }\n"
            + "}\n"
            + "module \"network\" {\n"
            + "  source = \"./network\"\n"
            + "}\n"
            + "moved {\n"
            + "  from = aws_instance.old\n"
            + "  to   = aws_instance.web\n"
            + "}\n"
            + "output \"ip\" {\n"
            + "  value = aws_instance.web[0].public_ip\n"
            + "}\n";

    @Test
    public void terraform_configuration_countsTheBlocksAndNotCommentsHeredocsOrAttributes() {
        // act, assert
        assertThat(count(TERRAFORM, "resources", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "data sources", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "modules", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "variables", TERRAFORM_SOURCE), is(2));
        assertThat(count(TERRAFORM, "outputs", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "locals blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "providers", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "backends", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "dynamic blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "count and for_each", TERRAFORM_SOURCE), is(2));
        assertThat(count(TERRAFORM, "refactoring blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "sensitive values", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "Terragrunt includes and dependencies", TERRAFORM_SOURCE), is(0));
    }

    @Test
    public void terraform_terragruntBlocksAndAnUnclosedHeredoc_countTheBlocksAndRunToTheEnd() {
        // arrange
        final String terragrunt = "include \"root\" {\n  path = find_in_parent_folders()\n}\n"
                + "dependency \"vpc\" {\n  config_path = \"../vpc\"\n}\n";
        final String unclosed = "resource \"a\" \"b\" {\n  x = <<EOT\nresource \"c\" \"d\" {}\n";

        // act, assert
        assertThat(count(TERRAFORM, "Terragrunt includes and dependencies", terragrunt), is(2));
        assertThat(count(TERRAFORM, "resources", unclosed), is(1));
    }

    @Test
    public void terraform_referencesOnALineOfTheirOwn_areNoBlocks() {
        // arrange
        final String references = "resource \"aws_instance\" \"web\" {\n"
                + "  depends_on = [\n"
                + "    module.network,\n"
                + "    data.aws_caller_identity.current,\n"
                + "    provider.aws,\n"
                + "  ]\n"
                + "}\n";

        // act, assert
        assertThat(count(TERRAFORM, "resources", references), is(1));
        assertThat(count(TERRAFORM, "modules", references), is(0));
        assertThat(count(TERRAFORM, "data sources", references), is(0));
        assertThat(count(TERRAFORM, "providers", references), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ansible-facts">
    private static final String PLAYBOOK = "---\n"
            + "- name: Deploy the shop\n"
            + "  hosts: web\n"
            + "  become: true\n"
            + "  roles:\n"
            + "    - common\n"
            + "  tasks:\n"
            + "    - name: Copy the jar\n"
            + "      ansible.builtin.copy:\n"
            + "        src: shop.jar\n"
            + "        dest: \"/opt/{{ app_dir }}/shop.jar\"\n"
            + "      notify: restart shop\n"
            + "    - name: Run the migration\n"
            + "      ansible.builtin.shell: |\n"
            + "        - name: not a task\n"
            + "        ./migrate.sh\n"
            + "      register: migration\n"
            + "      when: migrate | bool\n"
            + "    # - name: old task\n"
            + "    - name: Open the ports\n"
            + "      community.general.ufw:\n"
            + "        port: \"{{ item }}\"\n"
            + "      loop: [8080, 8443]\n"
            + "    - ansible.builtin.include_tasks: extra.yml\n"
            + "    - name: Users\n"
            + "      ansible.builtin.user:\n"
            + "        name: \"{{ item }}\"\n"
            + "        password: !vault |\n"
            + "          $ANSIBLE_VAULT;1.1;AES256\n"
            + "      with_items: [a, b]\n"
            + "  handlers:\n"
            + "    - name: restart shop\n"
            + "      ansible.builtin.service:\n"
            + "        name: shop\n"
            + "- import_playbook: monitoring.yml\n";

    @Test
    public void ansible_playbook_countsPlaysTasksAndModulesAndNotTheScriptBody() {
        // act, assert
        assertThat(count(ANSIBLE, "plays", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "named tasks, handlers and plays", PLAYBOOK), is(6));
        assertThat(count(ANSIBLE, "FQCN module calls", PLAYBOOK), is(6));
        assertThat(count(ANSIBLE, "role inclusions", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "task and playbook inclusions", PLAYBOOK), is(2));
        assertThat(count(ANSIBLE, "handler notifications", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "privilege escalation", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "conditionals", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "loops", PLAYBOOK), is(2));
        assertThat(count(ANSIBLE, "registered results", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "vault-encrypted values", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "template expressions", PLAYBOOK), is(3));
    }

    @Test
    public void ansible_becomeQuotedOrCapitalized_countsAsPrivilegeEscalation() {
        // arrange
        final String play = "- hosts: a\n  become: \"yes\"\n- hosts: b\n  become: 'true'\n- hosts: c\n  become: Yes\n"
                + "- hosts: d\n  become: false\n";

        // act, assert
        assertThat(count(ANSIBLE, "privilege escalation", play), is(3));
    }

    @Test
    public void ansible_emptyBlockScalar_blanksNoSiblingKey() {
        // arrange: each empty scalar's next line is a sibling of its key, as deep as the key
        final String play = "- hosts: web\n"
                + "  tasks:\n"
                + "    - ansible.builtin.shell: >\n"
                + "      register: out\n"
                + "    - name: Notify\n"
                + "      ansible.builtin.command: >\n"
                + "      when: out is changed\n";

        // act, assert
        assertThat(count(ANSIBLE, "registered results", play), is(1));
        assertThat(count(ANSIBLE, "FQCN module calls", play), is(2));
        assertThat(count(ANSIBLE, "conditionals", play), is(1));
    }

    @Test
    public void ansible_loopItemsAndTemplatesInABlockScalar_countAsAnsibleReadsThem() {
        // arrange
        final String play = "- hosts: all\n"
                + "  tasks:\n"
                + "    - name: Users\n"
                + "      ansible.builtin.user:\n"
                + "        name: \"{{ item.name }}\"\n"
                + "      loop:\n"
                + "        - name: alice\n"
                + "          groups: admin\n"
                + "        - name: bob\n"
                + "    - name: Script\n"
                + "      ansible.builtin.shell: |\n"
                + "        echo {{ greeting }}\n"
                + "        # echo {{ commented }}\n"
                + "      with_items:\n"
                + "      - name: x\n";

        // act, assert
        // Users and Script; not the items of loop: and with_items:
        assertThat(count(ANSIBLE, "named tasks, handlers and plays", play), is(2));
        assertThat(count(ANSIBLE, "loops", play), is(2));
        // item.name and greeting, which Ansible renders in the script; not the commented-out one
        assertThat(count(ANSIBLE, "template expressions", play), is(2));
    }

    @Test
    public void ansible_dottedVariablesOfARolesDefaults_areNoModuleCalls() {
        // arrange
        final String defaults = "---\n"
                + "net.ipv4.ip_forward: 1\n"
                + "app.config.path: /etc/app\n"
                + "sysctl_settings:\n"
                + "  net.core.somaxconn: 1024\n";
        final String tasks = "---\n"
                + "- name: Forward\n"
                + "  ansible.posix.sysctl:\n"
                + "    name: net.ipv4.ip_forward\n"
                + "    value: '1'\n"
                + "- when: forward | bool\n"
                + "  name: Open the port\n"
                + "  community.general.ufw:\n"
                + "    rule: allow\n"
                + "- ansible.builtin.debug:\n"
                + "    msg: done\n";

        // act, assert
        assertThat(count(ANSIBLE, "FQCN module calls", defaults), is(0));
        // sysctl below its - name:, ufw below a name: as deep as it, debug as the list item
        assertThat(count(ANSIBLE, "FQCN module calls", tasks), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="compose-facts">
    private static final String COMPOSE_SOURCE = "services:\n"
            + "  shop:\n"
            + "    build: .\n"
            + "    image: shop:${TAG:-latest}\n"
            + "    ports:\n"
            + "      - \"8080:8080\"\n"
            + "    environment:\n"
            + "      DB_URL: jdbc:postgresql://db/shop\n"
            + "    env_file: .env\n"
            + "    depends_on:\n"
            + "      - db\n"
            + "    restart: unless-stopped\n"
            + "    deploy:\n"
            + "      replicas: 2\n"
            + "    secrets:\n"
            + "      - db_password\n"
            + "  db:\n"
            + "    image: postgres:16\n"
            + "    command: >\n"
            + "      postgres -c image: x\n"
            + "    healthcheck:\n"
            + "      test: [\"CMD\", \"pg_isready\"]\n"
            + "    volumes:\n"
            + "      - data:/var/lib/postgresql/data\n"
            + "    profiles: [full]\n"
            + "    networks: [back]\n"
            + "  # cache:\n"
            + "  #   image: redis\n"
            + "volumes:\n"
            + "  data: {}\n"
            + "networks:\n"
            + "  back: {}\n"
            + "secrets:\n"
            + "  db_password:\n"
            + "    file: ./db_password.txt\n";

    @Test
    public void compose_composeFile_countsServicesAndTheirKeysAndNotComments() {
        // act, assert
        assertThat(count(COMPOSE, "services with an image", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "services built here", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "port lists", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "dependencies between services", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "healthchecks", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "environment blocks and files", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "volume lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "network lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "secret lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "restart policies", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "profiles", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "deploy sections", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "variable substitutions", COMPOSE_SOURCE), is(1));
    }

    @Test
    public void compose_escapedDollar_isNoSubstitution() {
        // arrange
        final String compose = "services:\n"
                + "  app:\n"
                + "    image: app:${TAG}\n"
                + "    environment:\n"
                + "      LITERAL: $${NOT_A_VARIABLE}\n"
                + "      PRICE: $$${CURRENCY}\n";

        // act, assert
        // TAG, and CURRENCY after the escaped dollar sign; the escaped NOT_A_VARIABLE is none
        assertThat(count(COMPOSE, "variable substitutions", compose), is(2));
    }

    @Test
    public void compose_unbracedVariable_isASubstitution() {
        // arrange
        final String compose =
                "services:\n" + "  app:\n" + "    image: app:$TAG\n" + "    command: echo $$HOME $${PATH} $$$USER\n";

        // act, assert
        // TAG and USER after the escaped dollar sign; not the escaped HOME and PATH
        assertThat(count(COMPOSE, "variable substitutions", compose), is(2));
    }

    @Test
    public void compose_substitutionInABlockScalar_countsAndOneInAComment_doesNot() {
        // arrange
        final String compose = "services:\n"
                + "  app:\n"
                + "    image: app:${TAG}\n"
                + "    command: >\n"
                + "      sh -c \"echo ${GREETING} $$HOME\"\n"
                + "    entrypoint: |\n"
                + "      run $NAME\n"
                + "  # cache:\n"
                + "  #   image: redis:${REDIS_TAG}\n"
                + "    environment:\n"
                + "      A: x # was ${OLD}\n"
                + "      B: \"a#b${C}\"\n";

        // act, assert
        // TAG, GREETING and NAME in the block scalars, and C after a # that opens no comment
        assertThat(count(COMPOSE, "variable substitutions", compose), is(4));
        // the block scalars' bodies stay blanked for the keys
        assertThat(count(COMPOSE, "services with an image", compose), is(1));
    }

    @Test
    public void compose_emptyBlockScalar_blanksNoSiblingKey() {
        // arrange
        final String compose = "services:\n"
                + "  db:\n"
                + "    image: postgres:16\n"
                + "    command: >\n"
                + "    healthcheck:\n"
                + "      test: [\"CMD\", \"pg_isready\"]\n"
                + "    ports:\n"
                + "      - \"5432:5432\"\n";

        // act, assert
        assertThat(count(COMPOSE, "healthchecks", compose), is(1));
        assertThat(count(COMPOSE, "port lists", compose), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="camel-yaml-facts">
    private static final String CAMEL_ROUTES = "- beans:\n"
            + "    - name: pricing\n"
            + "      type: shop.Pricing\n"
            + "- route:\n"
            + "    id: orders\n"
            + "    from:\n"
            + "      uri: jms:queue:orders\n"
            + "      steps:\n"
            + "        - log: received ${body}\n"
            + "        - choice:\n"
            + "            when:\n"
            + "              - simple: ${header.priority} == 'high'\n"
            + "                steps:\n"
            + "                  - to:\n"
            + "                      uri: jms:queue:urgent\n"
            + "            otherwise:\n"
            + "              steps:\n"
            + "                - toD: jms:queue:${header.region}\n"
            + "        - split:\n"
            + "            simple: ${body.lines}\n"
            + "        - setBody:\n"
            + "            groovy: |\n"
            + "              - to: not a step\n"
            + "        - bean:\n"
            + "            ref: pricing\n"
            + "        # - wireTap: jms:queue:audit\n"
            + "- from:\n"
            + "    uri: timer:tick\n"
            + "    steps:\n"
            + "      - doTry:\n"
            + "          steps:\n"
            + "            - to: http://stock/refresh\n"
            + "- routeTemplate:\n"
            + "    id: poller\n"
            + "- rest:\n"
            + "    path: /orders\n"
            + "    get:\n"
            + "      - to: direct:list\n"
            + "    post:\n"
            + "      - to: direct:create\n";

    @Test
    public void camelYaml_routes_countsRoutesAndEipsAndNotTheScripts() {
        // act, assert
        assertThat(count(CAMEL_YAML, "routes", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "route templates", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "consumers (from)", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "producers (to, toD, wireTap, enrich, pollEnrich)", CAMEL_ROUTES), is(5));
        assertThat(count(CAMEL_YAML, "endpoint URIs", CAMEL_ROUTES), is(3));
        assertThat(count(CAMEL_YAML, "content-based routers (choice)", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "splitters, aggregators and other routing EIPs", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "processors and transformations", CAMEL_ROUTES), is(3));
        assertThat(count(CAMEL_YAML, "error handling", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "REST definitions", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "REST verb blocks (get, post, put, delete, patch, head)", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "bean declarations", CAMEL_ROUTES), is(1));
    }

    @Test
    public void camelYaml_emptyBlockScalar_blanksNoSiblingKey() {
        // arrange
        final String routes = "- from:\n"
                + "    uri: timer:tick\n"
                + "    description: >\n"
                + "    steps:\n"
                + "      - log: |\n"
                + "      - to: direct:a\n";

        // act, assert
        assertThat(count(CAMEL_YAML, "producers (to, toD, wireTap, enrich, pollEnrich)", routes), is(1));
        assertThat(count(CAMEL_YAML, "processors and transformations", routes), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="gradle-facts">
    private static final String GROOVY_BUILD = "plugins {\n"
            + "    id 'java-library'\n"
            + "    id \"org.springframework.boot\" version \"3.3.0\"\n"
            + "    alias(libs.plugins.spotless)\n"
            + "}\n"
            + "apply plugin: 'maven-publish'\n"
            + "repositories {\n"
            + "    mavenCentral()\n"
            + "    maven { url 'https://repo.example.org/releases' }\n"
            + "}\n"
            + "dependencies {\n"
            + "    implementation platform(libs.spring.bom)\n"
            + "    implementation 'org.slf4j:slf4j-api:2.0.13'\n"
            + "    api project(':core')\n"
            + "    // implementation 'old:lib:1.0'\n"
            + "    compileOnly libs.lombok\n"
            + "    annotationProcessor libs.lombok\n"
            + "    testImplementation \"org.junit.jupiter:junit-jupiter:5.10.2\"\n"
            + "}\n"
            + "configurations {\n"
            + "    implementation.extendsFrom(custom)\n"
            + "}\n"
            + "tasks.register('fatJar', Jar) {\n"
            + "    description = \"\"\"implementation 'x:y:1' in a string\"\"\"\n"
            + "}\n"
            + "task hello {\n"
            + "}\n";

    private static final String KOTLIN_BUILD = "plugins {\n"
            + "    `java-library`\n"
            + "    kotlin(\"jvm\") version \"2.0.0\"\n"
            + "    id(\"com.diffplug.spotless\")\n"
            + "}\n"
            + "dependencies {\n"
            + "    implementation(enforcedPlatform(libs.boot.bom))\n"
            + "    implementation(projects.core)\n"
            + "    runtimeOnly(\"org.postgresql:postgresql:42.7.3\")\n"
            + "    /* testImplementation(\"old:lib:1.0\") */\n"
            + "}\n"
            + "tasks.named<Test>(\"test\") {\n"
            + "    useJUnitPlatform()\n"
            + "}\n";

    @Test
    public void gradle_groovyBuildScript_countsItsDeclarations() {
        // act, assert
        assertThat(count(GRADLE, "plugins", GROOVY_BUILD), is(4));
        assertThat(count(GRADLE, "dependency declarations", GROOVY_BUILD), is(6));
        assertThat(count(GRADLE, "project dependencies", GROOVY_BUILD), is(1));
        assertThat(count(GRADLE, "platforms and BOMs", GROOVY_BUILD), is(1));
        assertThat(count(GRADLE, "repositories", GROOVY_BUILD), is(2));
        assertThat(count(GRADLE, "tasks registered, created or configured", GROOVY_BUILD), is(2));
        assertThat(count(GRADLE, "version catalog references", GROOVY_BUILD), is(4));
    }

    @Test
    public void gradle_kotlinBuildScriptAndSettingsScript_countsTheirDeclarations() {
        // arrange
        final String settings = "rootProject.name = \"shop\"\n"
                + "include(\"core\", \"web\")\n"
                + "include ':cli'\n"
                + "includeBuild(\"build-logic\")\n";

        // act, assert
        assertThat(count(GRADLE, "plugins", KOTLIN_BUILD), is(3));
        assertThat(count(GRADLE, "dependency declarations", KOTLIN_BUILD), is(3));
        assertThat(count(GRADLE, "project dependencies", KOTLIN_BUILD), is(1));
        assertThat(count(GRADLE, "platforms and BOMs", KOTLIN_BUILD), is(1));
        assertThat(count(GRADLE, "tasks registered, created or configured", KOTLIN_BUILD), is(1));
        // statements, not projects: include("core", "web") is one
        assertThat(count(GRADLE, "include and includeBuild statements", settings), is(3));
    }

    @Test
    public void gradle_kotlinDslCorePluginsAndAccessorTasks_count() {
        // arrange
        final String build = "plugins {\n"
                + "    java\n"
                + "    application // the launcher\n"
                + "    id(\"com.example.x\")\n"
                + "}\n"
                + "java {\n"
                + "    toolchain.languageVersion.set(JavaLanguageVersion.of(21))\n"
                + "}\n"
                + "application {\n"
                + "    mainClass.set(\"shop.Main\")\n"
                + "}\n"
                + "tasks.test {\n"
                + "    useJUnitPlatform()\n"
                + "}\n"
                + "tasks.jar { manifest { } }\n"
                + "tasks.getByName(\"build\")\n"
                + "val names = listOf(1).map { id -> id }\n";

        // act, assert
        // java, application, id("com.example.x"); not the java { } and application { } blocks
        assertThat(count(GRADLE, "plugins", build), is(3));
        assertThat(count(GRADLE, "plugins", "plugins { java; application; id(\"x\") }\n"), is(3));
        // tasks.test { }, tasks.jar { }, tasks.getByName
        assertThat(count(GRADLE, "tasks registered, created or configured", build), is(3));
    }

    @Test
    public void gradle_copySpecAndTestFilterIncludes_areNoIncludedBuilds() {
        // arrange
        final String build = "processResources {\n"
                + "    from('src/extra') {\n"
                + "        include '**/*.xml'\n"
                + "    }\n"
                + "}\n"
                + "test {\n"
                + "    include '**/*Test.class'\n"
                + "    filter { includeTestsMatching 'shop.*' }\n"
                + "}\n";
        final String settings =
                "pluginManagement {\n" + "    includeBuild(\"build-logic\")\n" + "}\n" + "include ':app'\n";

        // act, assert
        assertThat(count(GRADLE, "include and includeBuild statements", build), is(0));
        assertThat(count(GRADLE, "include and includeBuild statements", settings), is(2));
    }

    @Test
    public void gradle_dependenciesAfterABraceOrSemicolon_count() {
        // arrange
        final String build = "dependencies { implementation 'a:b:1'; testImplementation 'c:d:2' }\n";

        // act, assert
        assertThat(count(GRADLE, "dependency declarations", build), is(2));
    }

    @Test
    public void gradle_pluginDeclarationsPublicationsAndTaskReferences_areNoPluginsRepositoriesOrTasks() {
        // arrange
        final String build = "plugins {\n"
                + "    id 'java-gradle-plugin'\n"
                + "    id 'maven-publish'\n"
                + "}\n"
                + "gradlePlugin {\n"
                + "    plugins {\n"
                + "        greeting {\n"
                + "            id 'org.example.greeting'\n"
                + "            implementationClass = 'org.example.GreetingPlugin'\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "publishing {\n"
                + "    publications {\n"
                + "        maven(MavenPublication) {\n"
                + "            pom {\n"
                + "                developers {\n"
                + "                    developer {\n"
                + "                        id 'jdoe'\n"
                + "                        name 'John Doe'\n"
                + "                    }\n"
                + "                    developer { name 'Jane Roe'; id 'jroe' }\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "    repositories {\n"
                + "        maven { url 'https://repo.example.org' }\n"
                + "    }\n"
                + "}\n"
                + "tasks.register('docs') {\n"
                + "    dependsOn tasks.named('javadoc')\n"
                + "    finalizedBy(tasks.named(\"zip\"))\n"
                + "}\n";

        // act, assert
        // the two of the plugins block; not the plugin's own id nor a developer's
        assertThat(count(GRADLE, "plugins", build), is(2));
        // maven { url }; not the publication maven(MavenPublication)
        assertThat(count(GRADLE, "repositories", build), is(1));
        // tasks.register; not the tasks dependsOn and finalizedBy refer to
        assertThat(count(GRADLE, "tasks registered, created or configured", build), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="properties-facts">
    @Test
    public void properties_propertiesFile_countsKeysAndNotComments() {
        // arrange
        final String properties = "# server.port=1\n"
                + "! legacy=1\n"
                + "server.port=8080\n"
                + "spring.datasource.url = jdbc:postgresql://db:5432/shop\n"
                + "spring.datasource.password=${DB_PASSWORD}\n"
                + "app.api-token: abc\n"
                + "greeting=Hello\n"
                + "\n";

        // act, assert
        assertThat(count(PROPERTIES, "keys (approx)", properties), is(5));
        assertThat(count(PROPERTIES, "keys naming a secret", properties), is(2));
        assertThat(count(PROPERTIES, "placeholders", properties), is(1));
        assertThat(count(PROPERTIES, "URLs", properties), is(1));
    }

    @Test
    public void properties_keysStartingWithASecretWord_countAsSecrets() {
        // arrange
        final String properties = "password=x\n"
                + "secret.key=abc\n"
                + "token: t\n"
                + "credentials.file=/etc/c\n"
                + "db.password=y\n"
                + "greeting=the password is not here\n";

        // act, assert
        assertThat(count(PROPERTIES, "keys naming a secret", properties), is(5));
    }

    @Test
    public void properties_continuationLinesAndWhitespaceSeparators_countAsTheLoaderReadsThem() {
        // arrange
        final String properties = "# a comment does not continue \\\n"
                + "server.port=8080\n"
                + "welcome message\n"
                + "greeting = Hello, \\\n"
                + "    world=1 \\\n"
                + "    password=not a key \\\n"
                + "    secret=neither\n"
                + "app.list: a, \\\\\n"
                + "next.key=1\n"
                + "db.password hunter2\n";

        // act, assert
        // server.port, welcome, greeting, app.list (its \\ is an escaped backslash), next.key, db.password
        assertThat(count(PROPERTIES, "keys (approx)", properties), is(6));
        // db.password; not password and secret, which continue the value of greeting
        assertThat(count(PROPERTIES, "keys naming a secret", properties), is(1));
        // a key without a value is a key
        assertThat(count(PROPERTIES, "keys (approx)", "empty\nkey=\n"), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="powershell-facts">
    private static final String POWERSHELL_SCRIPT = "#Requires -Version 7\n"
            + "#requires -Modules Az.Accounts\n"
            + "<#\n"
            + ".SYNOPSIS\n"
            + "function Not-AFunction { }\n"
            + "#>\n"
            + "using module Shop.Tools\n"
            + "Import-Module Az.Storage\n"
            + ". ./common.ps1\n"
            + "class Order { [string] $Id }\n"
            + "function Deploy-Shop {\n"
            + "    [CmdletBinding()]\n"
            + "    param(\n"
            + "        [Parameter(Mandatory)] [string] $Environment\n"
            + "    )\n"
            + "    # function Old-Thing { }\n"
            + "    $note = \"function Fake-Thing in a string\"\n"
            + "    $body = @\"\n"
            + "function Here-String { }\n"
            + "\"@\n"
            + "    try {\n"
            + "        Get-AzStorageAccount -Name $Environment | ForEach-Object { $_ } | ConvertTo-Json\n"
            + "    } catch {\n"
            + "        Write-Error \"failed\"\n"
            + "        throw\n"
            + "    }\n"
            + "}\n"
            + "FUNCTION helper { }\n";

    @Test
    public void powershell_script_countsFunctionsAndRequirementsAndNotHelpOrStrings() {
        // act, assert
        assertThat(count(POWERSHELL, "requirements", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "functions", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "advanced functions", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "parameter blocks", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "mandatory parameters", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "classes", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "modules imported", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "dot-sourced scripts", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "try blocks", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "errors raised", POWERSHELL_SCRIPT), is(2));
        // Import-Module, Get-AzStorageAccount, ForEach-Object, ConvertTo-Json, Write-Error; Deploy-Shop is
        // only defined
        assertThat(count(POWERSHELL, "Verb-Noun command calls (approx)", POWERSHELL_SCRIPT), is(5));
    }

    @Test
    public void powershell_commandCallsInAnyCase_countAndAFunctionDefinitionDoesNot() {
        // arrange
        final String script = "function Get-Thing { get-childitem | where-object { $_ } | sort-object }\n"
                + "Filter Find-Item { $_ }\n"
                + "Get-Thing\n";

        // act, assert
        // get-childitem, where-object, sort-object and the call of Get-Thing; not the two definitions
        assertThat(count(POWERSHELL, "Verb-Noun command calls (approx)", script), is(4));
    }

    @Test
    public void powershell_quotedDotSourceAndOptionalParameters_countAsWritten() {
        // arrange
        final String script = ". \"$PSScriptRoot/common.ps1\"\n"
                + ". '.\\lib.ps1' -Verbose\n"
                + "# . ./old.ps1\n"
                + "param(\n"
                + "    [Parameter(Mandatory = $true)] [string] $A,\n"
                + "    [Parameter(Mandatory=$False)] [string] $B,\n"
                + "    [Parameter(Mandatory)] [string] $C\n"
                + ")\n";

        // act, assert
        assertThat(count(POWERSHELL, "dot-sourced scripts", script), is(2));
        // $A and $C; not Mandatory=$False
        assertThat(count(POWERSHELL, "mandatory parameters", script), is(2));
    }

    @Test
    public void powershell_stringOverSeveralLines_blanksItsLines() {
        // arrange
        final String script = "$usage = \"Usage:\n"
                + "function Fake-Double {\n"
                + "\"\n"
                + "$note = 'it''s\n"
                + "function Fake-Single {\n"
                + "'\n"
                + "function Real-Thing { }\n"
                + "# function Commented-Out { }\n";

        // act, assert
        assertThat(count(POWERSHELL, "functions", script), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="batch-facts">
    @Test
    public void batch_script_countsLabelsCallsAndVariablesAndNotComments() {
        // arrange
        final String script = "@echo off\n"
                + "setlocal enabledelayedexpansion\n"
                + "REM goto :old\n"
                + ":: call old.bat\n"
                + "@rem set OLD=1\n"
                + "set APP_HOME=%~dp0\n"
                + "set /a COUNT=0\n"
                + "if \"%1\"==\"\" goto :usage\n"
                + "call :build\n"
                + "for /f \"tokens=*\" %%a in (list.txt) do set /a COUNT+=1\n"
                + "for /r C:\\src %%f in (*.java) do echo %%f\n"
                + "if errorlevel 1 exit /b 1\n"
                + "goto :eof\n"
                + ":build\n"
                + "call mvn -B package\n"
                + "exit /b %errorlevel%\n"
                + ":usage\n"
                + "echo usage: build.bat target\n"
                + "EXIT /B 2\n";

        // act, assert
        assertThat(count(BATCH, "labels", script), is(2));
        assertThat(count(BATCH, "calls", script), is(2));
        assertThat(count(BATCH, "gotos", script), is(2));
        assertThat(count(BATCH, "variables set", script), is(3));
        assertThat(count(BATCH, "conditionals (approx)", script), is(2));
        assertThat(count(BATCH, "for loops", script), is(2));
        assertThat(count(BATCH, "local scopes", script), is(1));
        assertThat(count(BATCH, "exits", script), is(3));
        assertThat(count(BATCH, "error level checks", script), is(2));
    }

    @Test
    public void batch_remAfterAnAmpersand_isAComment() {
        // arrange
        final String script = "call :build & rem call :old\n" + "echo done & REM goto :skip\n";

        // act, assert
        assertThat(count(BATCH, "calls", script), is(1));
        assertThat(count(BATCH, "gotos", script), is(0));
    }

    @Test
    public void batch_exitLabelsAndEchoedText_areNoExitsCallsOrConditionals() {
        // arrange
        final String script = "@echo off\n"
                + "if \"%1\"==\"\" goto :exit\n"
                + "call :build || goto exit\n"
                + "echo if you call this, set nothing and exit\n"
                + "echo. & exit /b 0\n"
                + ":build\n"
                + "echo:for each %%f in (a) do call x\n"
                + "exit /b 1\n"
                + ":exit\n";

        // act, assert
        // exit /b 0 and exit /b 1; not the label :exit nor the gotos to it
        assertThat(count(BATCH, "exits", script), is(2));
        assertThat(count(BATCH, "gotos", script), is(2));
        assertThat(count(BATCH, "calls", script), is(1));
        assertThat(count(BATCH, "conditionals (approx)", script), is(1));
        assertThat(count(BATCH, "variables set", script), is(0));
        assertThat(count(BATCH, "labels", script), is(2));
    }

    @Test
    public void batch_labelEndingInErrorLevel_isNoErrorLevelCheck() {
        // arrange: lines of the stock gradlew.bat
        final String script = "if %ERRORLEVEL% equ 0 goto mainEnd\n"
                + ":fail\n"
                + "set EXIT_CODE=%ERRORLEVEL%\n"
                + "if %EXIT_CODE% equ 0 set EXIT_CODE=1\n"
                + "call :exitWithErrorLevel %EXIT_CODE%\n"
                + ":mainEnd\n"
                + "goto :eof\n"
                + ":exitWithErrorLevel\n"
                + "exit /b %1\n";

        // act, assert
        // the two %ERRORLEVEL%; not the label :exitWithErrorLevel nor the call to it
        assertThat(count(BATCH, "error level checks", script), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="npm-facts">
    /**
     * A {@code package.json} with every counted object, a nested {@code scripts} key that is no npm
     * script, an escaped quote and an empty object.
     */
    private static final String PACKAGE_JSON = "{\n"
            + "  \"name\": \"demo\",\n"
            + "  \"workspaces\": [\"packages/*\", \"tools\"],\n"
            + "  \"scripts\": {\n"
            + "    \"build\": \"tsc -p .\",\n"
            + "    \"test\": \"jest --config \\\"jest.json\\\"\",\n"
            + "    \"lint\" : \"eslint .\"\n"
            + "  },\n"
            + "  \"dependencies\": {\"express\": \"^4.19.2\", \"zod\": \"3.23.8\"},\n"
            + "  \"devDependencies\": {\n"
            + "    \"typescript\": \"5.6.3\",\n"
            + "    \"jest\": \"29.7.0\",\n"
            + "    \"eslint\": \"9.12.0\"\n"
            + "  },\n"
            + "  \"peerDependencies\": {\"react\": \">=18\"},\n"
            + "  \"optionalDependencies\": {},\n"
            + "  \"engines\": {\"node\": \">=20\", \"npm\": \">=10\"},\n"
            + "  \"tool\": {\"scripts\": {\"nested\": \"x\"}, \"after\": \"y\"}\n"
            + "}\n";

    @Test
    public void npm_packageJson_countsTheEntriesOfEachObject() {
        // act, assert
        assertThat(count(NPM, "scripts", PACKAGE_JSON), is(4));
        assertThat(count(NPM, "dependencies", PACKAGE_JSON), is(2));
        assertThat(count(NPM, "dev dependencies", PACKAGE_JSON), is(3));
        assertThat(count(NPM, "peer dependencies", PACKAGE_JSON), is(1));
        assertThat(count(NPM, "optional dependencies", PACKAGE_JSON), is(0));
        assertThat(count(NPM, "engines", PACKAGE_JSON), is(2));
        assertThat(count(NPM, "workspaces", PACKAGE_JSON), is(2));
    }

    @Test
    public void npm_nestedObject_countStopsAtTheObjectsEnd() {
        // arrange: the entry after the scripts object follows its }, not a comma between its entries
        final String nested = "{\"tool\": {\"scripts\": {\"a\": \"x\"}, \"after\": \"y\"}}";

        // act, assert
        assertThat(count(NPM, "scripts", nested), is(1));
    }

    /** A VS Code extension's contributions: two commands, a menu entry naming one of them, two tools. */
    private static final String EXTENSION_JSON = "{\n"
            + "  \"contributes\": {\n"
            + "    \"commands\": [\n"
            + "      {\"command\": \"demo.open\", \"title\": \"Open\"},\n"
            + "      {\"command\" : \"demo.close\",\n        \"title\": \"Close \\\"it\\\"\"}\n"
            + "    ],\n"
            + "    \"menus\": {\"commandPalette\": [{\"command\": \"demo.open\", \"when\": \"true\"}]},\n"
            + "    \"languageModelTools\": [\n"
            + "      {\"name\": \"demo_find\", \"modelDescription\": \"Finds.\"},\n"
            + "      {\"name\": \"demo_read\", \"modelDescription\" : \"Reads.\"}\n"
            + "    ]\n"
            + "  }\n"
            + "}\n";

    @Test
    public void npm_vsCodeExtension_countsTheContributedCommandsAndTools() {
        // act, assert
        assertThat(count(NPM, "contributed commands", EXTENSION_JSON), is(2));
        assertThat(count(NPM, "language model tools", EXTENSION_JSON), is(2));
    }

    @Test
    public void npm_contributedCommandsWithTheirKeysInAnyOrder_countEach() {
        // arrange: title first, a key between command and title, a nested icon object
        final String extension = "{\"contributes\": {\"commands\": [\n"
                + "  {\"title\": \"Open\", \"command\": \"demo.open\"},\n"
                + "  {\"command\": \"demo.close\", \"category\": \"Demo\", \"title\": \"Close\","
                + " \"icon\": {\"light\": \"a.svg\", \"dark\": \"b.svg\"}}\n"
                + "], \"keybindings\": [{\"command\": \"demo.open\", \"key\": \"ctrl+o\"}]}}\n";

        // act, assert
        assertThat(count(NPM, "contributed commands", extension), is(2));
    }

    @Test
    public void npm_plainPackage_countsNoContributions() {
        // act, assert
        assertThat(count(NPM, "contributed commands", PACKAGE_JSON), is(0));
        assertThat(count(NPM, "language model tools", PACKAGE_JSON), is(0));
    }
    // </editor-fold>
}
