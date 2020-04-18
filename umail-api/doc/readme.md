### springboot打包war到服务器上：
#### 1.打包步骤
参考同目录下的"打包成war"

#### 2.Spring Boot: Cannot access REST Controller on localhost (404)
解决方法：使用ComponentScan注解明确扫描包路径
```
@SpringBootApplication
@ComponentScan(basePackages = {"org.edu.mail.api"})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
```

#### 3.自定义的jar包未打包进war中
在pom.xml中配置如下
```
<!--war将lib中jar文件打包进去-->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-dependencies</id>
            <phase>compile</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/${project.build.finalName}/WEB-INF/lib</outputDirectory>
                <includeScope>system</includeScope>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### 4.加载conf.properties文件出现问题
```
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.edu.mail.api.controller.AccountController]: Constructor threw exception; nested exception is java.lang.NullPointerException
	at org.springframework.beans.BeanUtils.instantiateClass(BeanUtils.java:154) ~[spring-beans-4.3.6.RELEASE.jar:4.3.6.RELEASE]
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:89) ~[spring-beans-4.3.6.RELEASE.jar:4.3.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1147) ~[spring-beans-4.3.6.RELEASE.jar:4.3.6.RELEASE]
	... 30 common frames omitted
Caused by: java.lang.NullPointerException: null
	at java.util.Properties$LineReader.readLine(Unknown Source) ~[na:1.8.0_201]
	at java.util.Properties.load0(Unknown Source) ~[na:1.8.0_201]
	at java.util.Properties.load(Unknown Source) ~[na:1.8.0_201]
	at org.edu.mail.usage.UMailConfigure.<init>(UMailConfigure.java:16) ~[usage-1.0.jar:na]
	at org.edu.mail.api.controller.AccountController.<init>(AccountController.java:40) ~[classes/:1.0]
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) ~[na:1.8.0_201]
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(Unknown Source) ~[na:1.8.0_201]
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(Unknown Source) ~[na:1.8.0_201]
	at java.lang.reflect.Constructor.newInstance(Unknown Source) ~[na:1.8.0_201]
	at org.springframework.beans.BeanUtils.instantiateClass(BeanUtils.java:142) ~[spring-beans-4.3.6.RELEASE.jar:4.3.6.RELEASE]
	... 32 common frames omitted
```
解决方法：硬编码
@RestController
public class MessageController{
    private Properties conf = new Properties();
    public MessageController(){
        conf.setProperty("attach.directory", "d://umail//attach//");
    }
    ...
}
