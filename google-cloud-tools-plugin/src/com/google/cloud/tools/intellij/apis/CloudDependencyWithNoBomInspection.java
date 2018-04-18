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

package com.google.cloud.tools.intellij.apis;

import com.google.cloud.tools.libraries.json.CloudLibrary;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsInspection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.MavenDomUtil;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
/**
 * A {@link DomElementsInspection} that detects missing import of the google-cloud-java BOM when
 * there are google-cloud-java dependencies in the pom.xml.
 *
 * <p>Provides a quick-fix to add the BOM, and strips out the version tag from the dependency if
 * present.
 */
public class CloudDependencyWithNoBomInspection extends CloudBomInspection {

  private static final Logger logger = Logger.getInstance(CloudDependencyWithNoBomInspection.class);

  @Nullable
  @Override
  public String getStaticDescription() {
    return "Inspection that checks for a Google Cloud Dependency definition with no Google Cloud BOM";
  }

  @Override
  public void checkFileElement(
      DomFileElement<MavenDomProjectModel> domFileElement, DomElementAnnotationHolder holder) {

    checkDependencyWithNoBom(domFileElement.getRootElement(), holder);
  }

  private void checkDependencyWithNoBom(
      MavenDomProjectModel projectModel, DomElementAnnotationHolder holder) {
    Module module = projectModel.getModule();

    if (module == null) {
      return;
    }

    Set<CloudLibrary> cloudLibraries =
        CloudLibraryProjectState.getInstance(module.getProject()).getCloudLibraries(module);

    if (cloudLibraries.isEmpty()) {
      return;
    }

    if (CloudLibraryProjectState.getInstance(module.getProject())
        .getCloudLibraryBomVersion(module)
        .isPresent()) {
      return;
    }

    projectModel
        .getDependencies()
        .getDependencies()
        .forEach(
            dependency -> {
              if (isCloudLibraryDependency(dependency, cloudLibraries)) {
                holder.createProblem(
                    dependency,
                    HighlightSeverity.GENERIC_SERVER_ERROR_OR_WARNING,
                    "It is recommended to import the google-cloud-java BOM when using Google Cloud dependencies",
                    new AddBomAndStripVersionQuickFix(module));
              }
            });
  }

  /**
   * A {@link LocalQuickFix} that will add the google-cloud-java BOM to the pom.xml and delete the
   * version tag if specified.
   */
  private class AddBomAndStripVersionQuickFix implements LocalQuickFix {

    private Module module;

    AddBomAndStripVersionQuickFix(Module module) {
      this.module = module;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
      return "No google-cloud-bom found: add BOM";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      XmlTag xmlElement = (XmlTag) descriptor.getPsiElement();
      if (xmlElement == null) {
        logger.error(
            "Unexpected null xml element when attempting to apply DependencyWithNoBom "
                + "quick-fix");
        return;
      }

      addBom(project);

      // Strip out the version tag if present
      Stream.of(xmlElement.getChildren())
          .filter(childTag -> childTag instanceof XmlTag)
          .map(childTag -> (XmlTag) childTag)
          .filter(childTag -> "version".equals(childTag.getName()))
          .findFirst()
          .ifPresent(CloudDependencyWithNoBomInspection.this::stripVersion);
    }

    private void addBom(Project project) {
      MavenProject mavenProject = MavenProjectsManager.getInstance(project).findProject(module);
      if (mavenProject != null) {
        MavenDomProjectModel model =
            MavenDomUtil.getMavenDomProjectModel(project, mavenProject.getFile());
        if (model != null) {
          Optional<String> latestBomVersion =
              CloudApiMavenService.getInstance().getLatestBomVersion();

          if (latestBomVersion.isPresent()) {
            CloudLibraryDependencyWriter.writeNewBom(model, latestBomVersion.get());
          } else {
            logger.warn(
                "Error adding bom when applying DependencyWithNoBom quickfix since latest BOM "
                    + "could not be fetched from Maven Central");
          }
        } else {
          logger.warn(
              "Error adding bom when applying DependencyWithNoBom quickfix due to missing Maven Dom "
                  + "Model");
        }
      } else {
        logger.warn(
            "Error adding bom when applying DependencyWithNoBom quickfix because Maven project "
                + "could not be resolved");
      }
    }
  }
}
