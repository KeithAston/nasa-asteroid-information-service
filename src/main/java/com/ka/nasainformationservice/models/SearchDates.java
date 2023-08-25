package com.ka.nasainformationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SearchDates {
    @NotNull
    private String start_date;
    @NotNull
    private String end_date;
}
