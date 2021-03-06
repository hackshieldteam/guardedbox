package com.guardedbox.controller;

import static com.guardedbox.constants.Roles.ROLE_ACCOUNT;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.config.AuthenticationPrincipal;
import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.ChallengeDto;
import com.guardedbox.dto.OtpDto;
import com.guardedbox.dto.OtpResponseDto;
import com.guardedbox.dto.SessionInfoDto;
import com.guardedbox.dto.SignedChallengeResponseDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.service.ChallengeService;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.OtpService;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.AccountsService;

/**
 * Controller: Session Handling.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/session")
@Validated
public class SessionController {

    /** Indicates if environment is dev, based on property environment. */
    private final boolean dev;

    /** Property: security-parameters.challenge.execution-time. */
    private final long challengeExecutionTime;

    /** Property: security-parameters.otp.execution-time. */
    private final long otpExecutionTime;

    /** Property: security-parameters.login.execution-time. */
    private final long loginExecutionTime;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /** ChallengeService. */
    private final ChallengeService challengeService;

    /** OtpService. */
    private final OtpService otpService;

    /** Current Request. */
    private final HttpServletRequest request;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     *
     * @param dev Indicates if environment is dev, based on property environment.
     * @param challengeExecutionTime Property: security-parameters.challenge.execution-time.
     * @param otpExecutionTime Property: security-parameters.otp.execution-time.
     * @param loginExecutionTime Property: security-parameters.login.execution-time.
     * @param accountsService AccountsService.
     * @param sessionAccount SessionAccountService.
     * @param executionTimeService ExecutionTimeService.
     * @param challengeService ChallengeService.
     * @param otpService OtpService.
     * @param request Current Request.
     * @param session Current Session.
     */
    public SessionController(
            @Value("#{'${environment}' == 'dev'}") boolean dev,
            @Value("${security-parameters.challenge.execution-time}") long challengeExecutionTime,
            @Value("${security-parameters.otp.execution-time}") long otpExecutionTime,
            @Value("${security-parameters.login.execution-time}") long loginExecutionTime,
            @Autowired AccountsService accountsService,
            @Autowired SessionAccountService sessionAccount,
            @Autowired ExecutionTimeService executionTimeService,
            @Autowired ChallengeService challengeService,
            @Autowired OtpService otpService,
            @Autowired HttpServletRequest request,
            @Autowired HttpSession session) {
        this.dev = dev;
        this.challengeExecutionTime = challengeExecutionTime;
        this.otpExecutionTime = otpExecutionTime;
        this.loginExecutionTime = loginExecutionTime;
        this.accountsService = accountsService;
        this.sessionAccount = sessionAccount;
        this.executionTimeService = executionTimeService;
        this.challengeService = challengeService;
        this.otpService = otpService;
        this.request = request;
        this.session = session;
    }

    /**
     * @return The current session information.
     */
    @GetMapping()
    public SessionInfoDto getSessionInfo() {

        SessionInfoDto sessionInfoDto = new SessionInfoDto();
        AccountDto sessionAccountDto = sessionAccount.getAccount();

        if (sessionAccountDto != null) {
            sessionInfoDto.setAuthenticated(true);
            sessionInfoDto.setEmail(sessionAccountDto.getEmail());
        }

        return sessionInfoDto;

    }

    /**
     * @return A challenge.
     */
    @PostMapping("/challenge")
    public ChallengeDto getChallenge() {

        long startTime = System.currentTimeMillis();

        // Remove the challenge and the one time password from the current session.
        session.removeAttribute(SessionAttributes.CHALLENGE);
        session.removeAttribute(SessionAttributes.OTP);

        // Generate the challenge.
        ChallengeDto challengeDto = challengeService.generateChallenge();

        // Store it in the current session.
        session.setAttribute(SessionAttributes.CHALLENGE, challengeDto);

        // Fix execution time.
        executionTimeService.fix(startTime, challengeExecutionTime);

        // Return it.
        return challengeDto;

    }

    /**
     * Verifies the signed response to a previously requested challenge, and generates and sends a one time password.
     *
     * @param signedChallengeResponseDto Object with the signed response to a previously requested challenge.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/otp")
    public SuccessDto obtainOtp(
            @RequestBody(required = true) @Valid SignedChallengeResponseDto signedChallengeResponseDto) {

        long startTime = System.currentTimeMillis();

        try {

            // Check if a challenge was previously requested.
            ChallengeDto challengeDto = (ChallengeDto) session.getAttribute(SessionAttributes.CHALLENGE);
            if (challengeDto == null) {
                throw new ServiceException("Challenge is not stored in session");
            }

            // Remove the challenge and the one time password from the current session.
            session.removeAttribute(SessionAttributes.CHALLENGE);
            session.removeAttribute(SessionAttributes.OTP);

            // Verify the signed challenge response.
            if (!challengeService.verifySignedChallengeResponse(signedChallengeResponseDto, challengeDto)) {
                throw new ServiceException("Challenge response is incorrect");
            }

            // Generate and send the one time password.
            OtpDto otpDto = null;
            if (dev) {
                otpDto = new OtpDto();
                otpDto.setEmail(signedChallengeResponseDto.getEmail());
            } else {
                otpDto = otpService.generateAndSendOtp(signedChallengeResponseDto.getEmail());
            }

            // Store it in the current session.
            session.setAttribute(SessionAttributes.OTP, otpDto);

        } catch (Exception e) {
        }

        // Fix execution time.
        executionTimeService.fix(startTime, otpExecutionTime);

        // Successful result.
        return new SuccessDto(true);

    }

    /**
     * Performs a login.
     *
     * @param otpResponseDto Object with the response to a previously requested one time password.
     * @return Object indicating if the login was successful.
     */
    @PostMapping("/login")
    public SuccessDto login(
            @RequestBody(required = true) @Valid OtpResponseDto otpResponseDto) {

        long startTime = System.currentTimeMillis();

        try {

            // Check if a one time password was previously generated.
            OtpDto otpDto = (OtpDto) session.getAttribute(SessionAttributes.OTP);
            if (otpDto == null) {
                throw new ServiceException("One time password is not stored in session");
            }

            // Remove the challenge and the one time password from the current session.
            session.removeAttribute(SessionAttributes.CHALLENGE);
            session.removeAttribute(SessionAttributes.OTP);

            // Verify the one time password response.
            if (!dev && !otpService.verifyOtp(otpResponseDto, otpDto)) {
                throw new ServiceException("One time password response is incorrect");
            }

            // Set the authentication.
            AccountDto accountDto = accountsService.getAndCheckAccountByEmail(otpDto.getEmail());
            AuthenticationPrincipal authenticationPrincipal = new AuthenticationPrincipal(accountDto);
            List<GrantedAuthority> roles = Arrays.asList(ROLE_ACCOUNT);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationPrincipal, null, roles);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Change session ID (Cookie).
            request.changeSessionId();

            // Successful result.
            return new SuccessDto(true);

        } catch (Exception e) {

            return new SuccessDto(false);

        } finally {

            // Fix execution time.
            executionTimeService.fix(startTime, loginExecutionTime);

        }

    }

    /**
     * Performs a logout.
     *
     * @return Object indicating if the logout was successful.
     */
    @PostMapping("/logout")
    public SuccessDto logout() {

        session.invalidate();

        return new SuccessDto(true);

    }

}
