/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.alex.jirazapidemo.alex.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexTestCaseStep {

    private Long id;

    private Long testCase;

    private int number;

    private boolean shouldFail = false;

    private AlexSymbol symbol;

    private List<AlexParameterValue> parameterValues = new ArrayList<>();

    private String expectedResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestCase() {
        return testCase;
    }

    public void setTestCase(Long testCase) {
        this.testCase = testCase;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isShouldFail() {
        return shouldFail;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public AlexSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(AlexSymbol symbol) {
        this.symbol = symbol;
    }

    public List<AlexParameterValue> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<AlexParameterValue> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public List<AlexParameterValue> getVisibleParameterValues() {
        return parameterValues.stream().filter(val -> {
            return !val.getParameter().isPrivate() &&
                    val.getParameter().getType().equals("input");
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "AlexTestCaseStep{" +
                "id=" + id +
                ", testCase=" + testCase +
                ", number=" + number +
                ", shouldFail=" + shouldFail +
                ", symbol=" + symbol +
                ", parameterValues=" + parameterValues +
                '}';
    }
}
