/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.user.index;

import org.sonar.server.search.BaseDoc;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class UserDoc extends BaseDoc {

  public UserDoc(Map<String, Object> fields) {
    super(fields);
  }

  public String login() {
    return getField(UserIndexDefinition.FIELD_LOGIN);
  }

  public String name() {
    return getField(UserIndexDefinition.FIELD_NAME);
  }

  @Nullable
  public String email() {
    return getNullableField(UserIndexDefinition.FIELD_EMAIL);
  }

  public boolean active() {
    return (Boolean) getField(UserIndexDefinition.FIELD_ACTIVE);
  }

  @Nullable
  public List<String> scmAccounts() {
    return (List<String>) getNullableField(UserIndexDefinition.FIELD_SCM_ACCOUNTS);
  }

  public long createdAt() {
    return getField(UserIndexDefinition.FIELD_CREATED_AT);
  }

  public long updatedAt() {
    return getField(UserIndexDefinition.FIELD_UPDATED_AT);
  }

  public void setLogin(@Nullable String s) {
    setField(UserIndexDefinition.FIELD_LOGIN, s);
  }

  public void setName(@Nullable String s) {
    setField(UserIndexDefinition.FIELD_NAME, s);
  }

  public void setEmail(@Nullable String s) {
    setField(UserIndexDefinition.FIELD_EMAIL, s);
  }

  public void setActive(boolean b) {
    setField(UserIndexDefinition.FIELD_ACTIVE, b);
  }

  public void setScmAccounts(@Nullable List<String> s) {
    setField(UserIndexDefinition.FIELD_SCM_ACCOUNTS, s);
  }

  public void setCreatedAt(long l) {
    setField(UserIndexDefinition.FIELD_CREATED_AT, l);
  }

  public void setUpdatedAt(long l) {
    setField(UserIndexDefinition.FIELD_UPDATED_AT, l);
  }
}
