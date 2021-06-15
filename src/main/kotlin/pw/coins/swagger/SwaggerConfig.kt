package pw.coins.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }

}

//import io.swagger.annotations.Authorization;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Configuration
//public class SpringFoxConfig {
//
//    //    It's a hack because If I set the name of the apiKey ex. API_KEY_NAME = "JWT"
////    then it will be generated a json api with the property
////    securitySchemes: { JWT: { type: "apiKey",
////      name: "JWT", /* here name property is the name of the header and looks like unfortunately it's taken from the name of
////          the key not from the keyname parameter when creating an instance of the ApiKey
////          so I need to set the name of the key = "Authorization" since I want to send the token in the Authorization header */
////      in: "header" } },
//    public static final String API_KEY_NAME = "Authorization";
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .securitySchemes(Collections.singletonList(apiKey()))
//                .securityContexts(Collections.singletonList(securityContext()))
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build()
//                ;
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey(API_KEY_NAME, HttpHeaders.AUTHORIZATION, "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .operationSelector(operationContext -> {
//                    final Optional<Authorization> foundAnnotation = operationContext.findAnnotation(Authorization.class);
//                    return foundAnnotation.isPresent()
//                            && foundAnnotation.get().value().equals(API_KEY_NAME);
//                })
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        var authorizationScopes = new AuthorizationScope[]{
//                new AuthorizationScope("global", "accessEverything")
//        };
//        return Collections.singletonList(new SecurityReference(API_KEY_NAME, authorizationScopes));
//    }
//}