## Vanilla
[中文文档](https://github.com/ro4/vanilla/blob/main/README-zh.md)

[![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/ro4/vanilla/badges/quality-score.png?b=main)](https://scrutinizer-ci.com/g/ro4/vanilla/?branch=main)
[![Code Coverage](https://scrutinizer-ci.com/g/ro4/vanilla/badges/coverage.png?b=main)](https://scrutinizer-ci.com/g/ro4/vanilla/?branch=main)
[![Build Status](https://scrutinizer-ci.com/g/ro4/vanilla/badges/build.png?b=main)](https://scrutinizer-ci.com/g/ro4/vanilla/build-status/main)


Want use SpEL to do some complex validations? this project will help you.  

May you want try [SpEL Validation](https://github.com/ro4/spel-validation) instead, simpler.  

### Quickstart

#### 1. Maven dependency

First, add maven dependency to pom.xml of your project, you can find the latest version at Maven Central.

```xml

<dependency>
    <groupId>io.github.ro4</groupId>
    <artifactId>vanilla</artifactId>
    <version>{LATEST-VERSION}</version>
</dependency>
```

#### 2. Enable vanilla

Like most Spring libraries, we need to add the `@EnableVailla` annotation to `@Configration` class to turn it on:

```java

@EnableVanilla
@Configuration
public class SomeConfig {

}
```

#### 3. Basic example

Let's start with a simple DTO, user wants to submit a `ConfigDTO` which has three properties: name, min, and max, and is restricted by following rules:

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
    public String demo(@RequestBody ConfigDTO dto) {
        return "hello";
    }
}
```

We had archived the first goal: when the min value is greater than the max value, there will be a `CheckFailedException`. You may want to throw your business exception instead, read `4. Exception` section for more information.

As you can see, we use `#p0` to represent argument `dto` in SpEL expression, `0` means it's the first argument in the method invocation, `1` is second, and so on.  

Next, let's move on to the "name must be unique in database" restriction. We need a bean with a validation method like below:

```java

@Service("configService")
public class ConfigService {
    public boolean isUnique(String name) {
        return true; // some query here
    }
}
```

Then, add a new `@CheckRule`:

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

Use `@` to get a Spring bean and invoke its method, this is `SpEL` mechanism, you can visit the official document website for more information.

#### 4. Exception

If a `@CheckRule` fails(return false), a `CheckFailedException` will throw by default. It's configured in `DefaultExceptionProvider` class, you can implement `ExceptionProvider` and override `produce` method to return a custom exception. Then, register your own `ExceptionProvider` as a Spring bean, so Vanilla can auto-find and register it as an exception provider.

Here is a simple example:

```java

@Component
public class DemoExceptionProvider implements ExceptionProvider<ValidationException> {
    @Override
    public ValidationException produce(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            sb.append(error.getDefaultMessage()).append(",");
        }
        return new ValidationException(sb.toString());
    }
}

```

You may also need an `ExceptionHandler` to return a readable response:

```java

@RestControllerAdvice
public class DemoExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> exceptionHandler(ValidationException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", null);
        result.put("message", exception.getMessage());
        return result;
    }
}

```

tips:

1. DO NOT register more than one `ExceptionProvider` bean, Vanilla will be confused about which one should be used.
2. Exception MUST be a subclass of `RuntimeException` as `ExceptionProvider` interface has declared. This is because of Vanilla is based on Spring AOP, undeclared `checked exception` will be wrapped by `UndeclaredThrowableException`during proxy proceed.

### Annotation

#### @Checkable

`@Checkable` can be used on Method, it means this method's argument should be validated. You can modify the value of `stopOnFirstFailure` (default `false`) to true, validation processes will stop when meeting the first fail.

#### @CheckRule

`@CheckRule` is used to describe restrictions inside a `@Checkable`, multiple rules are supported, and each rule has five properties: `expression`, `andExpression`, `orExpression`, `message`, and `field`.     

When an expression is way too long, `andExpression` and `orExpression` can be used to split a long expression into several short expressions, you can also use `+` to do so, but not elegant.    

The expression will be built like below:

```groovy
 String expression = checkRule.expression();

if (!ObjectUtils.isEmpty(checkRule.andExpression())) {
    expression = String.format("(%s) && (%s)", expression, checkRule.andExpression());
}

if (!ObjectUtils.isEmpty(checkRule.orExpression())) {
    expression = String.format("(%s) || (%s)", expression, checkRule.orExpression());
}
return expression;
```