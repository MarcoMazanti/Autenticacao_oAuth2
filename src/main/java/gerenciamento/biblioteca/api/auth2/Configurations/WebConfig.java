package gerenciamento.biblioteca.api.auth2.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Interceptador interceptador;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptador)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/v3/api-docs/**"
                );
    }
}
