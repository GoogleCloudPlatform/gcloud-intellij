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

package com.google.cloud.tools.intellij.appengine.sdk;

import com.intellij.openapi.components.ServiceManager;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

/** IntelliJ configured service for providing the path to the Cloud SDK. */
public interface CloudSdkService {

  static CloudSdkService getInstance() {
    return ServiceManager.getService(CloudSdkService.class);
  }

  @Nullable
  Path getSdkHomePath();

  void setSdkHomePath(String path);

  /*enum SdkStatus {
    READY, INSTALLING, INVALID, NOT_AVAILABLE
  }

  static interface SdkStatusUpdateListener {
    void onSdkStatusChange(CloudSdkService sdkService, SdkStatus status);
  }

  abstract SdkStatus getStatus();

  abstract boolean installAutomatically();

  void addStatusUpdateListener(SdkStatusUpdateListener listener) {

  }*/
}
