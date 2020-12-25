package com.example.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <description>
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-12-25
 */
@Configuration
public class DocumentationConfig {
    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") String appName, @Value("${application.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description("This is a sample application. This application includes Spring Boot, Spring Security, Docker and MongoDB. For this sample, you can use the api key `special-key` to test the authorization filters.")
                        .termsOfService("https://www.termsofservicegenerator.net/live.php?token=x3DRggFu9NSONqno22mfjLw1914AoeUO")
                        .license(new License().name("GPL-2.0").url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.html")));
    }
}
