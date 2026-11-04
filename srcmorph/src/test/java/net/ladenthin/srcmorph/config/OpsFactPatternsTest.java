// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
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

    private static final List<AiFactCounter> OPENAPI = ProfileFactPatterns.counters("openapi-facts");

    private static final List<AiFactCounter> K8S = ProfileFactPatterns.counters("k8s-facts");

    private static final List<AiFactCounter> ACTIONS = ProfileFactPatterns.counters("actions-facts");

    private static final List<AiFactCounter> DOCKERFILE = ProfileFactPatterns.counters("dockerfile-facts");

    private static int count(final List<AiFactCounter> counters, final String label, final String source) {
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
        assertThat(count(OPENAPI, "paths", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "operations (approx)", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "operationIds", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "parameters", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "$ref references", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "object schemas", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "security schemes", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "deprecated operations and fields", OPENAPI_YAML), is(1));
    }

    @Test
    public void openapi_minifiedJson_countsTheSameKeys() {
        final String json = "{\"openapi\":\"3.0.3\",\"paths\":{\"/orders\":{\"get\":{\"operationId\":\"listOrders\","
                + "\"parameters\":[{\"in\":\"query\",\"name\":\"limit\"}]},\"post\":{\"operationId\":\"createOrder\"}}},"
                + "\"components\":{\"schemas\":{\"Order\":{\"type\":\"object\"}}}}";
        assertThat(count(OPENAPI, "paths", json), is(1));
        assertThat(count(OPENAPI, "operations (approx)", json), is(2));
        assertThat(count(OPENAPI, "operationIds", json), is(2));
        assertThat(count(OPENAPI, "parameters", json), is(1));
        assertThat(count(OPENAPI, "object schemas", json), is(1));
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
    public void k8s_countsResourcesAtColumnZeroAndNotACommentOrAQuotedManifest() {
        assertThat(count(K8S, "resources", MANIFEST), is(3));
        assertThat(count(K8S, "workloads", MANIFEST), is(1));
        assertThat(count(K8S, "Services, Ingresses and Routes", MANIFEST), is(1));
        assertThat(count(K8S, "ConfigMaps and Secrets", MANIFEST), is(1));
    }

    @Test
    public void k8s_countsContainersAndHelmActionsAndNotAHelmComment() {
        assertThat(count(K8S, "container images", MANIFEST), is(1));
        assertThat(count(K8S, "container ports", MANIFEST), is(1));
        assertThat(count(K8S, "probes", MANIFEST), is(1));
        assertThat(count(K8S, "secret references", MANIFEST), is(1));
        assertThat(count(K8S, "Helm control actions (approx)", MANIFEST), is(1));
        assertThat(count(K8S, ".Values references", MANIFEST), is(2));
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
    public void actions_countsJobsStepsActionsAndSecretNames() {
        assertThat(count(ACTIONS, "jobs on a runner", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "reusable workflow calls", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "steps (approx)", WORKFLOW), is(2));
        assertThat(count(ACTIONS, "actions used", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "secret references", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "cron schedules", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "matrices", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "permissions blocks", WORKFLOW), is(1));
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
    public void dockerfile_countsInstructionsAtTheStartOfALineInAnyCase() {
        assertThat(count(DOCKERFILE, "build stages", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "RUN instructions", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "environment entries", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "exposed ports", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "users", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "entrypoints and commands", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "healthchecks", DOCKERFILE_SOURCE), is(1));
    }
    // </editor-fold>
}
