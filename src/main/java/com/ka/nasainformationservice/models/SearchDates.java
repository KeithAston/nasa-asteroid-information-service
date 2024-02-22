package com.ka.nasainformationservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class SearchDates {
    @NotEmpty
    private String start_date;

    @NotEmpty
    private String end_date;
}
