package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Scope("singleton")
public class MvcConfig {

    private static String[] origins;
    private static Boolean allowCredentials;

    @Autowired
    public MvcConfig(@Value("${ORIGINS}") String[] origins, @Value("${ALLOW_CREDENTIALS}") Boolean allowCredentials) {
        MvcConfig.origins = origins;
        MvcConfig.allowCredentials = allowCredentials;
    }

    public static String[] getOrigins() {
        return origins;
    }

    public static Boolean getAllowCredentials() {
        return allowCredentials;
    }

}