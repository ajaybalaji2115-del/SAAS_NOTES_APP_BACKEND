package com.example.demo.config;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
@Autowired
 private JwtAuthenticationFilter jwtAuthenticationFilter;
 @Bean
public PasswordEncoder passwordEncoder() {
 return new BCryptPasswordEncoder();
}
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http
.csrf(csrf -> csrf.disable())
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
 .authorizeHttpRequests(auth -> auth

.requestMatchers("/users/login", "/health").permitAll()
 .requestMatchers(HttpMethod.POST, "/notes").hasAnyAuthority("ADMIN","MEMBER")
 .requestMatchers(HttpMethod.GET, "/notes", "/notes/**").hasAnyAuthority("ADMIN", "MEMBER")
 .requestMatchers(HttpMethod.PUT, "/notes/**").hasAnyAuthority("ADMIN", "MEMBER")
.requestMatchers(HttpMethod.DELETE, "/notes/**").hasAnyAuthority("ADMIN", "MEMBER")


.requestMatchers(HttpMethod.POST, "/tenants/*/upgrade").hasAuthority("ADMIN")
// Any other request must be authenticated
.anyRequest().authenticated()
 )
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
return http.build();
}
 @Bean
public UrlBasedCorsConfigurationSource corsConfigurationSource() {
 CorsConfiguration configuration = new CorsConfiguration();
configuration.setAllowedOrigins(Arrays.asList(
 "http://localhost:5173",
"http://localhost:3000",
"http://localhost:3001",
"https://saas-notes-app-frontend-two.vercel.app/" // change for your frontend
));
 configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
configuration.setAllowedHeaders(Arrays.asList("*"));
 configuration.setAllowCredentials(true);
 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
source.registerCorsConfiguration("/**", configuration);
return source;
}
}