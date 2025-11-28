package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPassword {

    @Size(min = 8, max = 16)
    private String currentPassword;
    @Size(min = 8, max = 16)
    private String newPassword;
}
