/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.batch;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.utils.KeyValueFormat.Converter;

import java.util.Map;

/**
 * @since 2.14
 */
@Beta
public class DefaultFileLinesContext implements FileLinesContext {

  private final SonarIndex index;
  private final Resource resource;

  /**
   * metric key -> line -> value
   */
  private final Map<String, Map<Integer, Object>> map = Maps.newHashMap();

  public DefaultFileLinesContext(SonarIndex index, Resource resource) {
    this.index = index;
    this.resource = resource;
  }

  public void setIntValue(String metricKey, int line, int value) {
    Preconditions.checkNotNull(metricKey);
    Preconditions.checkArgument(line > 0);

    setValue(metricKey, line, value);
  }

  public Integer getIntValue(String metricKey, int line) {
    Preconditions.checkNotNull(metricKey);
    Preconditions.checkArgument(line > 0);

    Map lines = map.get(metricKey);
    if (lines == null) {
      // not in memory, so load
      lines = loadData(metricKey, KeyValueFormat.newIntegerConverter());
      map.put(metricKey, lines);
    }
    return (Integer) lines.get(line);
  }

  public void setStringValue(String metricKey, int line, String value) {
    Preconditions.checkNotNull(metricKey);
    Preconditions.checkArgument(line > 0);
    Preconditions.checkNotNull(value);

    setValue(metricKey, line, value);
  }

  public String getStringValue(String metricKey, int line) {
    Preconditions.checkNotNull(metricKey);
    Preconditions.checkArgument(line > 0);

    Map lines = map.get(metricKey);
    if (lines == null) {
      // not in memory, so load
      lines = loadData(metricKey, KeyValueFormat.newStringConverter());
      map.put(metricKey, lines);
    }
    return (String) lines.get(line);
  }

  private Map<Integer, Object> getOrCreateLines(String metricKey) {
    Map<Integer, Object> lines = map.get(metricKey);
    if (lines == null) {
      lines = Maps.newHashMap();
      map.put(metricKey, lines);
    }
    return lines;
  }

  private void setValue(String metricKey, int line, Object value) {
    getOrCreateLines(metricKey).put(line, value);
  }

  public void save() {
    for (Map.Entry<String, Map<Integer, Object>> entry : map.entrySet()) {
      String metricKey = entry.getKey();
      Map<Integer, Object> lines = entry.getValue();
      if (shouldSave(lines)) {
        String data = KeyValueFormat.format(lines);
        Measure measure = new Measure(metricKey)
            .setPersistenceMode(PersistenceMode.DATABASE)
            .setData(data);
        index.addMeasure(resource, measure);
      }
    }
  }

  private Map loadData(String metricKey, Converter converter) {
    // FIXME no way to load measure only by key
    Measure measure = index.getMeasure(resource, new Metric(metricKey));
    if (measure == null || measure.getData() == null) {
      // no such measure
      return ImmutableMap.of();
    }
    return ImmutableMap.copyOf(KeyValueFormat.parse(measure.getData(), KeyValueFormat.newIntegerConverter(), converter));
  }

  /**
   * Checks that measure was not loaded.
   *
   * @see #loadData(String, Converter)
   */
  private boolean shouldSave(Map<Integer, Object> lines) {
    return !(lines instanceof ImmutableMap);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("map", map)
        .toString();
  }

}
