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
            return val.getParameter().getParameterType().equals("STRING") &&
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
