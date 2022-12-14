package ru.tokarev.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tokarev.security.jwt.AuthTokenFilter;
import ru.tokarev.security.jwt.JwtUtils;

@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@EnableWebSecurity
@Configuration
public class SpringSecurityConfiguration {

    private final UserDetailsService userDetailsService;

    private final JwtUtils jwtUtils;

    @Value("${start_endpoint_prefix}")
    private String startEndpointPrefix;

    @Value("${endpoint_signup_prefix}")
    private String endpointSignupPrefix;

    @Value("${endpoint_login_prefix}")
    private String endpointLoginPrefix;

    @Autowired
    public SpringSecurityConfiguration(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .antMatcher(startEndpointPrefix)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, endpointLoginPrefix, endpointSignupPrefix).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

