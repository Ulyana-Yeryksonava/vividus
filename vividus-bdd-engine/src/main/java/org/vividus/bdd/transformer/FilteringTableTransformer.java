/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.bdd.transformer;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.model.ExamplesTableProperties;
import org.vividus.bdd.util.ExamplesTableProcessor;

@Named("FILTERING")
public class FilteringTableTransformer implements ExtendedTableTransformer
{
    private static final String BY_MAX_COLUMNS_PROPERTY = "byMaxColumns";
    private static final String BY_MAX_ROWS_PROPERTY = "byMaxRows";
    private static final String BY_COLUMNS_NAMES_PROPERTY = "byColumnNames";

    private Supplier<ExamplesTableFactory> examplesTableFactory;

    @Override
    public String transform(String tableAsString, ExamplesTableProperties properties)
    {
        String byMaxColumns = properties.getProperties().getProperty(BY_MAX_COLUMNS_PROPERTY);
        String byMaxRows = properties.getProperties().getProperty(BY_MAX_ROWS_PROPERTY);
        String byColumnNames = properties.getProperties().getProperty(BY_COLUMNS_NAMES_PROPERTY);
        checkArgument(byMaxColumns != null || byMaxRows != null || byColumnNames != null,
                "At least one of the following properties should be specified: '%s', '%s', '%s'",
                BY_MAX_COLUMNS_PROPERTY, BY_MAX_ROWS_PROPERTY, BY_COLUMNS_NAMES_PROPERTY);
        checkArgument(!(byMaxColumns != null && byColumnNames != null),
                "Conflicting properties declaration found: '%s' and '%s'",
                BY_MAX_COLUMNS_PROPERTY, BY_COLUMNS_NAMES_PROPERTY);
        ExamplesTable examplesTable = examplesTableFactory.get().createExamplesTable(tableAsString);

        List<String> headerValues = examplesTable.getHeaders();
        int columnsLimit = byMaxColumns == null
                ? headerValues.size() : Math.min(headerValues.size(), Integer.parseInt(byMaxColumns));
        List<String> filteredColumnsNames = byColumnNames == null ? headerValues.subList(0, columnsLimit)
                : Arrays.stream(StringUtils.split(byColumnNames, ';')).map(String::trim).collect(Collectors.toList());
        Set<String> filteredHeaders = new HashSet<>(filteredColumnsNames);

        List<Map<String, String>> result = filterByHeaders(filteredHeaders, headerValues,
                getFilteredRows(byMaxRows, examplesTable));

        List<List<String>> resultRows = result.stream()
                .map(TreeMap::new)
                .map(Map::values)
                .map(ArrayList::new)
                .collect(Collectors.toList());

        return ExamplesTableProcessor.buildExamplesTable(filteredHeaders, resultRows, properties, true, true);
    }

    private List<Map<String, String>> filterByHeaders(Set<String> filteredHeaders, List<String> headerValues,
            List<Map<String, String>> result)
    {
        Set<String> headersForDeleting = new HashSet<>(headerValues);
        headersForDeleting.removeAll(filteredHeaders);
        result.stream().map(m -> m.keySet().removeAll(headersForDeleting))
            .collect(Collectors.toList());
        return result;
    }

    private List<Map<String, String>> getFilteredRows(String byMaxRows, ExamplesTable examplesTable)
    {
        return Optional.ofNullable(byMaxRows)
                .map(Integer::parseInt)
                .filter(m -> m < examplesTable.getRowCount())
                .map(m -> examplesTable.getRows().subList(0, m))
                .orElseGet(examplesTable::getRows);
    }

    public void setExamplesTableFactory(Supplier<ExamplesTableFactory> examplesTableFactory)
    {
        this.examplesTableFactory = examplesTableFactory;
    }
}
