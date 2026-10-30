package com.techdrawer.amy.service;

import com.techdrawer.amy.model.User;
import com.techdrawer.amy.model.Validation;
import com.techdrawer.amy.repository.ValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public void activationCode(User user) {

        SecureRandom random = new SecureRandom();
        Integer randomInt = random.nextInt(999999);
        String code = String.format("%06d", randomInt);

        Instant creation = Instant.now();
        Instant expiration = creation.plus(5, ChronoUnit.MINUTES);

        Validation validation = new Validation();

        validation.setCode(code);
        validation.setCreation(creation);
        validation.setExpiration(expiration);
        validation.setUser(user);

        validationRepository.save(validation);

        notificationService.sendValidation(validation);

    }


}
