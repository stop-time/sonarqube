/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.task.projectanalysis.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.apache.commons.lang.StringUtils;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.computation.task.projectanalysis.analysis.AnalysisMetadataHolder;

/**
 * Cache a map between component keys and uuids in the merge branch
 */
public class MergeBranchComponentUuids {
  private final AnalysisMetadataHolder analysisMetadataHolder;
  private final DbClient dbClient;
  private Map<String, String> uuidsByKey;

  public MergeBranchComponentUuids(AnalysisMetadataHolder analysisMetadataHolder, DbClient dbClient) {
    this.analysisMetadataHolder = analysisMetadataHolder;
    this.dbClient = dbClient;
  }

  private void loadMergeBranchComponents() {
    String mergeBranchUuid = analysisMetadataHolder.getBranch().get().getMergeBranchUuid().get();

    uuidsByKey = new HashMap<>();
    try (DbSession dbSession = dbClient.openSession(false)) {

      List<ComponentDto> components = dbClient.componentDao().selectByProjectUuid(mergeBranchUuid, dbSession);
      for (ComponentDto dto : components) {
        uuidsByKey.put(dto.getKey(), dto.uuid());
      }
    }
  }

  @CheckForNull
  public String getUuid(String dbKey) {
    if (uuidsByKey == null) {
      loadMergeBranchComponents();
    }

    String cleanComponentKey = removeBranchFromKey(dbKey);
    return uuidsByKey.get(cleanComponentKey);
  }

  private static String removeBranchFromKey(String componentKey) {
    return StringUtils.substringBeforeLast(componentKey, ComponentDto.BRANCH_KEY_SEPARATOR);
  }
}
