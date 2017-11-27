/*
 * Copyright 2017 Google Inc. All Rights Reserved.
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

package com.google.cloud.tools.intellij.project;

import com.google.cloud.tools.intellij.login.CredentialedUser;
import com.google.cloud.tools.intellij.login.Services;
import com.google.cloud.tools.intellij.login.ui.GoogleLoginIcons;
import com.google.cloud.tools.intellij.util.GctBundle;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent.EventType;

/**
 * ProjectSelector allows the user to select a GCP project id. It shows selected project name and
 * user account information. To change selection it uses {@link ProjectSelectionDialog} to call into
 * {@link com.google.cloud.tools.intellij.login.IntegratedGoogleLoginService} to get the set of
 * credentialed users and then into resource manager to get the set of projects.
 */
public class ProjectSelector extends JPanel {
  static final int ACCOUNT_ICON_SIZE = 16;

  private final List<ProjectSelectionListener> projectSelectionListeners = new ArrayList<>();

  private HyperlinkLabelWithStateAccess projectNameLabel;
  private HyperlinkLabelWithStateAccess accountInfoLabel;
  private JBLabel projectAccountSeparatorLabel;

  private CloudProject cloudProject;

  public ProjectSelector() {
    initUi();
    setSelectedProject(null);
  }

  /** @return project selection or null if no project is selected. */
  public CloudProject getSelectedProject() {
    return cloudProject;
  }

  /**
   * Updates component UI and state with the new project and user account information.
   *
   * @param cloudProject New project/account information, null to clear selected project.
   */
  public void setSelectedProject(CloudProject cloudProject) {
    this.cloudProject = cloudProject;
    updateProjectAndUserInformation(cloudProject);
  }

  /**
   * Adds a {@link ProjectSelectionListener} to this class's internal list of listeners. All
   * ProjectSelectionListeners are notified when the selection is changed to a valid project.
   *
   * @param projectSelectionListener the listener to add
   */
  public void addProjectSelectionListener(ProjectSelectionListener projectSelectionListener) {
    projectSelectionListeners.add(projectSelectionListener);
  }

  /**
   * Removes a {@link ProjectSelectionListener} from this class's internal list of listeners.
   *
   * @param projectSelectionListener the listener to remove
   */
  public void removeProjectSelectionListener(ProjectSelectionListener projectSelectionListener) {
    projectSelectionListeners.remove(projectSelectionListener);
  }

  private void initUi() {
    // layout - in the center of panel, horizontal line of project/account labels in a panel
    // ends with a fixed size browse button.
    setLayout(new BorderLayout());

    JPanel staticInfoPanel = new JPanel();
    staticInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
    staticInfoPanel.setBorder(UIManager.getBorder("TextField.border"));

    projectNameLabel = new HyperlinkLabelWithStateAccess();
    projectNameLabel.addHyperlinkListener(
        (event) -> {
          if (event.getEventType() == EventType.ACTIVATED) {
            handleOpenProjectSelectionDialog();
          }
        });
    staticInfoPanel.add(projectNameLabel);

    projectAccountSeparatorLabel = new JBLabel("/");
    projectAccountSeparatorLabel.setVisible(false /* only visible when project is selected. */);
    staticInfoPanel.add(projectAccountSeparatorLabel);

    accountInfoLabel = new HyperlinkLabelWithStateAccess();
    accountInfoLabel.addHyperlinkListener(
        (event) -> {
          if (event.getEventType() == EventType.ACTIVATED) {
            handleOpenProjectSelectionDialog();
          }
        });
    staticInfoPanel.add(accountInfoLabel);

    ComponentWithBrowseButton<JPanel> componentWithBrowseButton =
        new ComponentWithBrowseButton<>(
            staticInfoPanel, (actionEvent) -> handleOpenProjectSelectionDialog());

    add(componentWithBrowseButton);
  }

  @VisibleForTesting
  void handleOpenProjectSelectionDialog() {
    ProjectSelectionDialog projectSelectionDialog = createProjectSelectionDialog(this);
    CloudProject newSelection = projectSelectionDialog.showDialog(cloudProject);

    // if null, it means no change or user cancelled selection dialog - no update required.
    if (newSelection != null) {
      setSelectedProject(newSelection);
      notifyProjectSelectionListeners();
    }
  }

  private void updateProjectAndUserInformation(CloudProject selection) {
    if (selection == null || Strings.isNullOrEmpty(selection.getProjectName())) {
      projectNameLabel.setHyperlinkText(
          GctBundle.getString("project.selector.no.selected.project"));
      accountInfoLabel.setHyperlinkText("");
      accountInfoLabel.setIcon(null);
      projectAccountSeparatorLabel.setVisible(false);

    } else {
      projectNameLabel.setHyperlinkText(selection.getProjectName());
      projectAccountSeparatorLabel.setVisible(true);
      // first just show account email, then expand with name/picture if this account is signed in.
      accountInfoLabel.setHyperlinkText(selection.getGoogleUsername());
      Optional<CredentialedUser> loggedInUser =
          Services.getLoginService().getLoggedInUser(selection.getGoogleUsername());
      if (loggedInUser.isPresent()) {
        accountInfoLabel.setHyperlinkText(
            loggedInUser.get().getName() + " (" + loggedInUser.get().getEmail() + ")");
      }
      accountInfoLabel.setIcon(
          GoogleLoginIcons.getScaledUserIcon(ACCOUNT_ICON_SIZE, loggedInUser.orElse(null)));
    }
  }

  private void notifyProjectSelectionListeners() {
    projectSelectionListeners.forEach(listener -> listener.projectSelected(cloudProject));
  }

  /** Hyperlink label that provides access to its text and icon for testing purposes. */
  @VisibleForTesting
  static final class HyperlinkLabelWithStateAccess extends HyperlinkLabel {

    private String text;
    private Icon icon;

    @Override
    public void setHyperlinkText(String text) {
      this.text = text;
      super.setHyperlinkText(text);
    }

    @Override
    public void setIcon(Icon icon) {
      this.icon = icon;
      super.setIcon(icon);
    }

    public String getText() {
      return text;
    }

    public Icon getIcon() {
      return icon;
    }
  }

  @VisibleForTesting
  ProjectSelectionDialog createProjectSelectionDialog(Component parent) {
    return new ProjectSelectionDialog();
  }

  @VisibleForTesting
  HyperlinkLabelWithStateAccess getProjectNameLabel() {
    return projectNameLabel;
  }

  @VisibleForTesting
  HyperlinkLabelWithStateAccess getAccountInfoLabel() {
    return accountInfoLabel;
  }

  @VisibleForTesting
  JBLabel getProjectAccountSeparatorLabel() {
    return projectAccountSeparatorLabel;
  }
}
