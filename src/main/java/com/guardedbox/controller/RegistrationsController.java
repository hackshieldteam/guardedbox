package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.MINING_NONCE_LENGTH;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.CreateRegistrationDto;
import com.guardedbox.dto.MinedChallengeResponseDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.CryptoCaptchaService;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.transactional.RegistrationsService;

/**
 * Controller: Registrations.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/registrations")
@Validated
public class RegistrationsController {

    /** Property: security-parameters.registration.execution-time. */
    private final long createRegistrationExecutionTime;

    /** RegistrationsService. */
    private final RegistrationsService registrationsService;

    /** CryptoCaptchaService. */
    private final CryptoCaptchaService cryptoCaptchaService;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /**
     * Constructor with Attributes.
     *
     * @param createRegistrationExecutionTime Property: security-parameters.registration.execution-time.
     * @param registrationsService RegistrationsService.
     * @param cryptoCaptchaService CryptoCaptchaService.
     * @param executionTimeService ExecutionTimeService.
     */
    public RegistrationsController(
            @Value("${security-parameters.registration.execution-time}") long createRegistrationExecutionTime,
            @Autowired RegistrationsService registrationsService,
            @Autowired CryptoCaptchaService cryptoCaptchaService,
            @Autowired ExecutionTimeService executionTimeService) {
        this.createRegistrationExecutionTime = createRegistrationExecutionTime;
        this.registrationsService = registrationsService;
        this.cryptoCaptchaService = cryptoCaptchaService;
        this.executionTimeService = executionTimeService;
    }

    /**
     * @param token A registration token.
     * @param minedChallengeResponse Mined challenge response.
     * @return The Registration corresponding to the introduced token.
     */
    @GetMapping()
    public RegistrationDto getRegistration(
            @RequestParam(name = "token", required = true) @NotBlank @Pattern(regexp = ALPHANUMERIC_PATTERN) @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH) String token,
            @RequestParam(name = "mined-challenge-response", required = true) @NotBlank @Pattern(regexp = BASE64_PATTERN) @Size(min = MINING_NONCE_LENGTH, max = MINING_NONCE_LENGTH) String minedChallengeResponse) {

        // Verify the crypto-captcha.
        cryptoCaptchaService.verify(new MinedChallengeResponseDto(minedChallengeResponse));

        // Return the Registration.
        return registrationsService.getAndCheckRegistrationByToken(token);

    }

    /**
     * Creates a Registration, and sends its token.
     *
     * @param createRegistrationDto Object with the necessary data to create a Registration and send its token.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping()
    public SuccessDto createRegistration(
            @RequestBody(required = true) @Valid CreateRegistrationDto createRegistrationDto) {

        long startTime = System.currentTimeMillis();

        // Verify the crypto-captcha.
        cryptoCaptchaService.verify(createRegistrationDto);

        // Create Registration.
        registrationsService.createRegistration(createRegistrationDto);

        // Fix execution time.
        executionTimeService.fix(startTime, createRegistrationExecutionTime);

        // Successful result.
        return new SuccessDto(true);

    }

}
