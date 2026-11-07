// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * The content of a srcmorph configuration file (JSON or YAML), the one file both the CLI and the Maven
 * plugin are configured by: the run configuration under {@code srcMorph} with the file rules, the rule
 * of each aggregation phase, and the two keys only the CLI reads ({@code command}, {@code modelFilter}).
 * Read with {@link SrcMorphConfigurationFileLoader}.
 */
@ToString
public class SrcMorphConfigurationFile {

    private @Nullable String command;

    private String modelFilter = "";

    private SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();

    private @Nullable List<RoutingRule> packageRules;

    private @Nullable List<RoutingRule> projectRules;

    /** Creates an empty configuration file content, every default applied. */
    public SrcMorphConfigurationFile() {
        // no-op
    }

    /**
     * Returns the CLI command to run; the plugin, whose goal is the command, ignores it.
     *
     * @return the command name, or {@code null} for the CLI's default
     */
    public @Nullable String getCommand() {
        return command;
    }

    /**
     * Sets the CLI command to run.
     *
     * @param command the command name
     */
    public void setCommand(final @Nullable String command) {
        this.command = command;
    }

    /**
     * Returns the filter of the CLI's {@code ListModels} command; the plugin ignores it.
     *
     * @return the filter, empty for none
     */
    public String getModelFilter() {
        return modelFilter;
    }

    /**
     * Sets the filter of the CLI's {@code ListModels} command.
     *
     * @param modelFilter the filter
     */
    public void setModelFilter(final String modelFilter) {
        this.modelFilter = modelFilter;
    }

    /**
     * Returns the run configuration, with the file rules ({@code rules}) of the
     * {@code generate}, {@code calibrate} and {@code check} goals.
     *
     * @return the run configuration
     */
    public SrcMorphConfiguration getSrcMorph() {
        return srcMorph;
    }

    /**
     * Sets the run configuration.
     *
     * @param srcMorph the run configuration
     */
    public void setSrcMorph(final SrcMorphConfiguration srcMorph) {
        this.srcMorph = srcMorph;
    }

    /**
     * Returns the rule of the package phase ({@code aggregate-packages}): exactly one.
     *
     * @return the rules, or {@code null} when unset
     */
    public @Nullable List<RoutingRule> getPackageRules() {
        return packageRules;
    }

    /**
     * Sets the rule of the package phase.
     *
     * @param packageRules the rules
     */
    public void setPackageRules(final @Nullable List<RoutingRule> packageRules) {
        this.packageRules = packageRules;
    }

    /**
     * Returns the rule of the project phase ({@code aggregate-project}): none (no overview) or one.
     *
     * @return the rules, or {@code null} when unset
     */
    public @Nullable List<RoutingRule> getProjectRules() {
        return projectRules;
    }

    /**
     * Sets the rule of the project phase.
     *
     * @param projectRules the rules
     */
    public void setProjectRules(final @Nullable List<RoutingRule> projectRules) {
        this.projectRules = projectRules;
    }
}
