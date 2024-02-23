package com.ka.nasainformationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class SearchDates {
    @NotEmpty
    private String start_date;

    @NotEmpty
    private String end_date;
}
