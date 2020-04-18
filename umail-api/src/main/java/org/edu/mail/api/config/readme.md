> 前后端分离项目中要使用ajax访问网络资源，如果访问的不是同域的资源，会报错
<font color="red">Access to XMLHttpRequest at 'http://localhost:8080/api/test' from origin 'http://localhost:8088' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.</font>

解决方法是在服务器端设置Access-Control-Allow-Origin，springboot中已经考虑到这一点，详见[https://spring.io/blog/2015/06/08/cors-support-in-spring-framework](https://spring.io/blog/2015/06/08/cors-support-in-spring-framework)

本文只列出两种全局配置方法：两种选一种即可
**第一种：**
```
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
        }
 }
```
**第二种：**

```
@Configuration
public class MyConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}
 ```