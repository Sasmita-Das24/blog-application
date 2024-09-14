package com.mountblue.blog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select name, password, 1 as enabled from user where name=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select name, role from user where name=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(configurer ->
                        configurer
                                // Allow everyone to access these URLs
                                .requestMatchers("/**").permitAll()

//                                .requestMatchers("/login", "/register", "/posts").permitAll()
//
//                                // Allow only authenticated users with roles ADMIN or AUTHOR to create posts
//                                .requestMatchers(HttpMethod.POST, "/posts").hasAnyRole("ADMIN", "AUTHOR")
//                                .requestMatchers("/posts/**").hasAnyRole("ADMIN", "AUTHOR")
//                                .requestMatchers(HttpMethod.GET, "/create-post","/newpost").hasAnyRole("ADMIN", "AUTHOR")

                                // Any other requests need to be authenticated
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form ->
                        form.loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/posts", true)  // Redirect to posts after successful login
                                .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return httpSecurity.build();
    }
}
