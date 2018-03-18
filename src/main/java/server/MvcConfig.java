//package server;
//
//import org.apache.catalina.filters.CorsFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//
////@Configuration
//////@PropertySource("classpath:/com/myco/app.properties")
////@PropertySource("classpath:app.properties")
////public class MvcConfig {
////    @Autowired
////    Environment env;
////
////    @Value("${ORIGINS}")
////    private String [] origins;
////
////    @Value("${ALLOW_CREDENTIALS}")
////    private String allowCredentials;
////
////    MvcConfig() {
////        System.out.println(origins);
////        System.out.println(allowCredentials);
////    }
////
//////    @Bean
//////    public FilterRegistrationBean corsFilter() {
//////        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//////        final CorsConfiguration config = new CorsConfiguration();
//////        config.setAllowCredentials(true);
//////        config.addAllowedOrigin("http://domain1.com");
//////        config.addAllowedHeader("*");
//////        config.addAllowedMethod("*");
//////        source.registerCorsConfiguration("/**", config);
//////        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//////        bean.setOrder(0);
//////        return bean;
//////    }
////}
////
//////@Configuration
//////@PropertySource("classpath:/com/myco/app.properties")
//////public class AppConfig {
//////    @Autowired
//////    Environment env;
//////
//////    @Bean
//////    public TestBean testBean() {
//////        TestBean testBean = new TestBean();
//////        testBean.setName(env.getProperty("testbean.name"));
//////        return testBean;
//////    }
//////}
