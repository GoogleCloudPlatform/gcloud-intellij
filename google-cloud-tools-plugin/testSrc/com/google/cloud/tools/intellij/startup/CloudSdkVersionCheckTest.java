/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

package com.google.cloud.tools.intellij.startup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.internal.process.ProcessRunnerException;
import com.google.cloud.tools.appengine.cloudsdk.serialization.CloudSdkVersion;
import com.google.cloud.tools.intellij.appengine.sdk.CloudSdkService;
import com.google.cloud.tools.intellij.appengine.sdk.CloudSdkValidationResult;
import com.google.cloud.tools.intellij.testing.BasePluginTestCase;
import com.google.common.collect.Sets;

import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudSdkVersionCheckTest extends BasePluginTestCase {

  private CloudSdkVersionCheckForTesting checker;
  private Path fakeSdkPath;

  @Mock private CloudSdkService cloudSdkServiceMock;
  @Mock private Project projectMock;


  @Before
  public void setUp() throws ProcessRunnerException {
    registerService(CloudSdkService.class, cloudSdkServiceMock);

    fakeSdkPath = Paths.get("/");
    when(cloudSdkServiceMock.getSdkHomePath()).thenReturn(fakeSdkPath);

    checker = new CloudSdkVersionCheckForTesting();
  }

  @Test
  public void testNotifyIfCloudSdkNotSupported_isSupported() {
    when(cloudSdkServiceMock.validateCloudSdk(fakeSdkPath))
        .thenReturn(new HashSet<CloudSdkValidationResult>());
    checker.runActivity(projectMock);

    assertFalse(checker.hasShownNotification());
  }

  @Test
  public void testNotifyIfCloudSdkNotSupported_notSupported() {
    when(cloudSdkServiceMock.validateCloudSdk(fakeSdkPath))
        .thenReturn(Sets.newHashSet(CloudSdkValidationResult.CLOUD_SDK_VERSION_NOT_SUPPORTED));

    checker.runActivity(projectMock);
    assertTrue(checker.hasShownNotification());
  }

  @Test
  public void testNotifyIfCloudSdkNotSupported_sdkNotFound() {
    when(cloudSdkServiceMock.validateCloudSdk(fakeSdkPath))
        .thenReturn(Sets.newHashSet(CloudSdkValidationResult.CLOUD_SDK_NOT_FOUND));

    checker.runActivity(projectMock);
    assertFalse(checker.hasShownNotification());
  }

  @Test
  public void testNotifyIfCloudSdkNotSupported_nullSdkPath() {

    when(cloudSdkServiceMock.getSdkHomePath()).thenReturn(null);

    checker.runActivity(projectMock);

    // should not even bother trying to validate
    verify(cloudSdkServiceMock, times(0)).validateCloudSdk(any(CloudSdk.class));
    verify(cloudSdkServiceMock, times(0)).validateCloudSdk(any(Path.class));
    assertFalse(checker.hasShownNotification());
  }

  // Extend the class under test so that we can report whether the notification has been shown
  class CloudSdkVersionCheckForTesting extends CloudSdkVersionCheck {
    private boolean hasShownNotification;

    public CloudSdkVersionCheckForTesting() {
      hasShownNotification = false;
    }

    @Override
    void showNotification() {
      hasShownNotification = true;
    }

    public boolean hasShownNotification() {
      return hasShownNotification;
    }
  }
}
