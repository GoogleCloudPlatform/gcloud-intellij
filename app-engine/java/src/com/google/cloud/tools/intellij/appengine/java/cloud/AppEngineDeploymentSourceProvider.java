/*
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.intellij.appengine.java.cloud;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.remoteServer.configuration.deployment.DeploymentSource;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * An extension point for collecting {@link DeploymentSource deploymentSources}. Implementing
 * extensions can hook into this to provide custom deployment source collection logic.
 */
public interface AppEngineDeploymentSourceProvider {
  ExtensionPointName<AppEngineDeploymentSourceProvider> EP_NAME =
      ExtensionPointName.create("com.google.gct.core.appEngineDeploymentSourceProvider");

  List<DeploymentSource> getDeploymentSources(@NotNull Project project);
}
