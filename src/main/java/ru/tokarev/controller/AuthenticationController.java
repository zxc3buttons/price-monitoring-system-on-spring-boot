package ru.tokarev.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tokarev.dto.ApiErrorDto;
import ru.tokarev.dto.userdto.AuthenticationRequestDto;
import ru.tokarev.dto.userdto.LoginResponseDto;
import ru.tokarev.security.UserDetailsImpl;
import ru.tokarev.security.jwt.JwtUtils;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping(value = "/api")
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorized",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
    })
    public ResponseEntity<Object> login(@Valid @RequestBody AuthenticationRequestDto requestDto) {

        log.info("POST request for /login with username {} and password {}", requestDto.getUsername(),
                requestDto.getPassword());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        LoginResponseDto loginResponseDto = new LoginResponseDto(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);

        log.info("Response for POST request with data: {}, {}, {}, {}, {}, {}",
                loginResponseDto.getId(), loginResponseDto.getUsername(), loginResponseDto.getEmail(),
                loginResponseDto.getToken(), loginResponseDto.getRoles(), loginResponseDto.getType());

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }
}
