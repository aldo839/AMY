package com.techdrawer.amy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OrderRegistrationDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30)
    private String ownerFirstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30)
    private String ownerLastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+ ]{9,15}$", message = "Phone number must be valid")
    private String ownerPhoneNumber;

    @Email(message = "Email must be valid")
    private String ownerEmail;

    @NotBlank(message = "Shipping address is required")
    @Size(min = 5, max = 100, message = "Shipping address must contain between 5 and 100 characters")
    private String shippingAddress;

}
