package de.alex.alexforjira.api.alex.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexParameterizedSymbol {

    private AlexSymbol symbol;

    private List<AlexParameterValue> parameterValues = new ArrayList<>();

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

    public List<AlexParameterValue> getVisibleParameterValues() {
        return parameterValues.stream().filter(val -> {
            return !val.getParameter().isPrivate() &&
                    val.getParameter().getType().equals("input");
        }).collect(Collectors.toList());
    }
}
