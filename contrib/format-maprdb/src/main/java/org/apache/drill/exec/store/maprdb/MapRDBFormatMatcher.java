/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.maprdb;

import java.io.IOException;

import org.apache.drill.exec.planner.logical.DrillTable;
import org.apache.drill.exec.planner.logical.DynamicDrillTable;
import org.apache.drill.exec.store.dfs.DrillFileSystem;
import org.apache.drill.exec.store.dfs.FileSelection;
import org.apache.drill.exec.store.dfs.FileSystemPlugin;
import org.apache.drill.exec.store.dfs.FormatMatcher;
import org.apache.drill.exec.store.dfs.FormatPlugin;
import org.apache.drill.exec.store.dfs.FormatSelection;
import org.apache.hadoop.fs.FileStatus;

import com.mapr.fs.MapRFileStatus;

public class MapRDBFormatMatcher extends FormatMatcher {

  private final FormatPlugin plugin;

  public MapRDBFormatMatcher(FormatPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean supportDirectoryReads() {
    return false;
  }

  public DrillTable isReadable(DrillFileSystem fs,
      FileSelection selection, FileSystemPlugin fsPlugin,
      String storageEngineName, String userName) throws IOException {
    FileStatus status = selection.getFirstPath(fs);
    if (!isFileReadable(fs, status)) {
      return null;
    }

    return new DynamicDrillTable(fsPlugin, storageEngineName, userName,
        new FormatSelection(getFormatPlugin().getConfig(), selection));
  }

  @Override
  public boolean isFileReadable(DrillFileSystem fs, FileStatus status) throws IOException {
    return (status instanceof MapRFileStatus) &&  ((MapRFileStatus) status).isTable();
  }

  @Override
  public FormatPlugin getFormatPlugin() {
    return plugin;
  }
}
