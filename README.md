## Vanilla

Vanilla is a java library that tries to make params validation of spring boot projects easy to use.

### Quickstart

#### 1. Maven dependency

First, add maven dependency to pom.xml of your project, you can find the latest version at Maven Central.

```xml

<dependency>
    <groupId>me.ro4</groupId>
    <artifactId>vanilla</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 2. Enable vanilla

Like most spring libraries, we need to add the `@EnableVailla` annotation to `@Configration` class to turn it on.

```java

@EnableVanilla
@Configuration
public class SomeConfig {

}
```

#### 3. Basic example

Let's start with a simple DTO, user wants to submit a `ConfigDTO` which has three properties: key, min, and max, here is
restrictions:

* Max must be greater than min
* Name must be unique in the database

```java
public class ConfigDTO {
    private String name;

    private Integer min;

    private Integer max;

    // getter setter 
}
```

```java

@RestController
public class DemoController {
    @PostMapping("/demo")
    @Checkable(@CheckRule(expression = "#p0.max >= #p0.min", message = "max must greater than min"))
    public String demo(@Validated @RequestBody ConfigDTO dto) {
        return "hello";
    }
}
```

We had archived the first goal, if the min value is bigger than the max value, there will be a `CheckFailedException`.
We can customize our business exception easily, which will be talked about later.  
As you can see, we use `#p0` to represent param `dto` in SpEL expression. `0` means it's the first argument in the
method invocation, `1` is second, etc...  
Let's move on to the "name must be unique in database" restriction. We need a bean with a validation method like below:

```java

@Service("configService")
public class ConfigService {
    public boolean isUnique(String name) {
        return true; // some query here
    }
}
```

```java

@RestController
public class DemoController {
    @PostMapping("/demo")
    @Checkable({
            @CheckRule(expression = "#p0.max >= #p0.min", message = "max must greater than min"),
            @CheckRule(expression = "@configService.isUnique(#p0.name)", message = "name already exists")
    })
    public String demo(@Validated @RequestBody ConfigDTO dto) {
        return "hello";
    }
}
```

Use '@' to get a spring bean and invoke the method, this is `SpEL` mechanism, visit the official document website for
more information.

#### 4. Exception

If a `@CheckRule` failed(return false), a `CheckFailedException` will throw by default. It's configured
in `DefaultExceptionProvider` class, you can implement `ExceptionProvider` and override `produce` method to return a
custom exception. Then, register your own `ExceptionProvider` as a spring bean, so Vanilla can auto-find and register as
an exception provider. Here is a simple example:

```java

@Component
public class DemoExceptionProvider implements ExceptionProvider<IllegalArgumentException> {
    @Override
    public IllegalArgumentException produce(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            sb.append(error.getDefaultMessage()).append(",");
        }
        return new IllegalArgumentException(sb.toString());
    }
}

```

tips:

1. DO NOT register more than one `ExceptionProvider` bean, Vanilla will be confused about which one should be used.
2. Exception should be a subclass of `RuntimeException` as `ExceptionProvider` interface has declared. This is because
   of Vanilla is based on Spring AOP, any `checked exception` will be wrapped by `UndeclaredThrowableException` during
   proxy proceed.

### Annotation

#### @Checkable

`@Checkable` can be used on Method, it means this method's params should be valid. You can modify the value
of `stopOnFirstFailure` (default `false`) to true, validation processes will stop when meeting the first reject.

#### @CheckRule

`@CheckRule` is used to describe restrictions inside a `@Checkable`, multiple rules are supported. Each rule has three
properties: `expression`, `message`, and `field`.