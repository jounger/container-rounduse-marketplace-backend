package com.crm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.crm.security.jwt.AuthEntryPointJwt;
import com.crm.security.jwt.AuthTokenFilter;
import com.crm.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  
  @Autowired
  private AuthEntryPointJwt authEntryPointJwt;
  
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/api/**").allowedOrigins("http://localhost:8080"); // base on frontend server port
          }
      };
  }
  
  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }
  
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
      .exceptionHandling().authenticationEntryPoint(authEntryPointJwt).and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeRequests().antMatchers("/api/auth/**").permitAll()
      .antMatchers("/api/test/**").permitAll()
      .antMatchers("/api/admin/**").authenticated()
      .anyRequest().authenticated();
    
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
  
}
