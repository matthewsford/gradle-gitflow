/*
 * Copyright 2015 Max Kaeufer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.amkay.gradle.gitflow.strategy

import com.github.zafarkhaja.semver.Version
import com.github.amkay.gradle.gitflow.dsl.GitflowPluginExtension
import com.github.amkay.gradle.gitflow.version.NearestVersionLocator
import com.github.amkay.gradle.gitflow.version.VersionWithGitflowBranchBuilder
import org.ajoberstar.grgit.Grgit

/**
 * The strategy to use when one of Gitflow's <code>hotfix</code> branches is the current branch.
 *
 * @author Max Kaeufer
 */
class BranchHotfixStrategy extends Strategy {

    private static final String CONFIG_PREFIX_HOTFIX  = 'hotfix'
    private static final String DEFAULT_PREFIX_HOTFIX = 'hotfix/'


    @Override
    protected Version doInfer(final Grgit grgit, final GitflowPluginExtension ext) {
        def nearestVersion = new NearestVersionLocator().locate(grgit)

        def matcher = (grgit.branch.current.name =~ $/^${getHotfixPrefix(grgit)}(.*)/$)
        def hotfix = matcher[ 0 ][ 1 ]

        new VersionWithGitflowBranchBuilder(nearestVersion)
          .branch("${ext.preReleaseIds.hotfix}.$hotfix")
          .distanceFromRelease()
          .sha(grgit, ext)
          .dirty(grgit, ext)
          .build()
    }

    @Override
    boolean canInfer(final Grgit grgit) {
        grgit.branch.current.name.startsWith getHotfixPrefix(grgit)
    }

    private static String getHotfixPrefix(final Grgit grgit) {
        getPrefix(grgit, CONFIG_PREFIX_HOTFIX) ?: DEFAULT_PREFIX_HOTFIX
    }

}
