package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.List;

/**
 * Конфигурация безопасности веб‑приложения на основе Spring Security.
 *
 * <p>Данный класс определяет:
 * <ul>
 *   <li>сервис управления пользователями ({@link UserDetailsManager});</li>
 *   <li>цепочку фильтров безопасности ({@link SecurityFilterChain});</li>
 *   <li>настройки CORS ({@link CorsConfigurationSource});</li>
 *   <li>кодировщик паролей ({@link PasswordEncoder}).</li>
 * </ul>
 *
 * @see Configuration
 */
@Configuration
public class WebSecurityConfig {

    /**
     * Список URL‑путей, для которых аутентификация не требуется (белый список).
     *
     * <p>Включает:
     * <ul>
     *   <li>ресурсы Swagger для документации API;</li>
     *   <li>страницы входа и регистрации.</li>
     * </ul>
     */
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register"
    };

    /**
     * Создаёт и настраивает сервис управления пользователями на основе JDBC.
     *
     * <p>Настраивает SQL‑запросы для:
     * <ul>
     *   <li>получения пользователя по email;</li>
     *   <li>получения ролей пользователя;</li>
     *   <li>проверки существования пользователя.</li>
     * </ul>
     *
     * @param dataSource источник данных для подключения к базе
     * @return настроенный экземпляр {@link UserDetailsManager}
     * @see JdbcUserDetailsManager
     */
    @Bean
    public UserDetailsManager userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT email, password, enabled FROM users WHERE email = ?");
        manager.setAuthoritiesByUsernameQuery("SELECT email, role FROM users WHERE email = ?");
        manager.setUserExistsSql("SELECT email FROM users WHERE email =?");
        return manager;
    }

    /**
     * Определяет цепочку фильтров безопасности для HTTP‑запросов.
     *
     * <p>Настройки включают:
     * <ul>
     *   <li>отключение CSRF‑защиты;</li>
     *   <li>разрешение доступа без аутентификации для путей из {@link #AUTH_WHITELIST};</li>
     *   <li>обязательную аутентификацию для путей /ads/**, /users/**, /images/**;</li>
     *   <li>включение CORS с настройками из {@link #corsConfigurationSource()};</li>
     *   <li>базовую HTTP‑аутентификацию.</li>
     * </ul>
     *
     * @param http объект конфигурации безопасности HTTP
     * @return настроенная цепочка фильтров {@link SecurityFilterChain}
     * @throws Exception при ошибках конфигурации
     * @see HttpSecurity
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .requestMatchers(AUTH_WHITELIST)
                                        .permitAll()
                                        .requestMatchers("/ads/**", "/users/**", "/images/**")
                                        .authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Создаёт источник конфигурации CORS для управления кросс‑доменными запросами.
     *
     * <p>Настройки CORS:
     * <ul>
     *   <li>разрешённые origins: http://localhost:3000, http://localhost:8080;</li>
     *   <li>разрешённые методы: GET, POST, PUT, PATCH, DELETE;</li>
     *   <li>разрешённые заголовки: все (*);</li>
     *   <li>разрешено использование учётных данных (cookies, авторизации).</li>
     * </ul>
     *
     * @return экземпляр {@link CorsConfigurationSource} с настроенными правилами CORS
     * @see CorsConfiguration
     * @see UrlBasedCorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Предоставляет кодировщик паролей на основе алгоритма BCrypt.
     *
     * <p>BCrypt — криптографическая функция для хеширования паролей с солью,
     * обеспечивающая защиту от перебора и радужных таблиц.
     *
     * @return экземпляр {@link BCryptPasswordEncoder} для кодирования и проверки паролей
     * @see PasswordEncoder
     * @see BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
