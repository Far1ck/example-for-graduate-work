package ru.skypro.homework.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrUpdateAd {

    @Size(min = 4, max = 32)
    private String title;
    @Min(value = 0)
    @Max(value = 10000000)
    private int price;
    @Size(min = 8, max = 64)
    private String description;
}
