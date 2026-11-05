// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.util.List;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFile;
import org.jspecify.annotations.Nullable;

/** The rules of the configuration file a goal runs with. */
enum RuleSection {

    /** The file rules ({@code srcMorph.rules}): {@code generate}, {@code calibrate}. */
    FILES {
        @Override
        @Nullable
        List<RoutingRule> rulesOf(final SrcMorphConfigurationFile file) {
            return file.getSrcMorph().getRules();
        }
    },

    /** The package rule ({@code packageRules}): {@code aggregate-packages}. */
    PACKAGE {
        @Override
        @Nullable
        List<RoutingRule> rulesOf(final SrcMorphConfigurationFile file) {
            return file.getPackageRules();
        }
    },

    /** The project rule ({@code projectRules}): {@code aggregate-project}. */
    PROJECT {
        @Override
        @Nullable
        List<RoutingRule> rulesOf(final SrcMorphConfigurationFile file) {
            return file.getProjectRules();
        }
    };

    /**
     * Returns the section's rules.
     *
     * @param file the content of the configuration file
     * @return the rules, or {@code null} when the file has none there
     */
    abstract @Nullable List<RoutingRule> rulesOf(SrcMorphConfigurationFile file);
}
