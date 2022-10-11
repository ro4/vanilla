## Vanilla

本项目主要实现了使用 SpEL 进行校验参数的功能。

[TOC]

### Quickstart

#### 1. Maven 依赖

首先，添加 Maven 依赖到你的项目里，最新版本可在中央仓库里进行搜索。

```xml

<dependency>
    <groupId>me.ro4</groupId>
    <artifactId>vanilla</artifactId>
    <version>{LATEST-VERSION}</version>
</dependency>
```

#### 2. 启用 vanilla

然后，像其他 Spring 库一样，在 `@Configuration` 注解的类上添加 `@EnableVanilla` 以启用本库。

```java

@EnableVanilla
@Configuration
public class SomeConfig {

}
```

#### 3. 一个简单示例

思考一个简单的场景，程序接受一个 `ConfigDTO` 输入，`ConfigDTO` 有三个属性：name, min 及 max。它们存在以下限制：

* max 的值必须比 min 的值大
* name 在数据库中唯一

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
    @Checkable(@CheckRule(expression = "#p0.max >= #p0.min", message = "max 的值必须比 min 的值大"))
    public String demo(@RequestBody ConfigDTO dto) {
        return "hello";
    }
}
```

如以上代码片段所示，只需要简单注解下，我们实现了 max 的值必须比 min
的值大的限制，如果这个时候传的值违反此限制，框架会抛出一个 `CheckFailedException` 异常。
在实际项目中，如果需要定义抛出自定义校验异常，可以参考`4. 异常处理`章节。  
在 expression 中，我们使用了 `#p0` 来代表传入的 `dto` 参数，`0` 表示第一个参数，`1` 表示第二个参数，就像数组下标一样。

接下来我们将实现数据库中 name 唯一这个限制，首先需要写一个 Spring bean 方法来进行数据库查询校验：

```java

@Service("configService")
public class ConfigService {
    public boolean isUnique(String name) {
        return true; // some query here
    }
}
```

在上面的示例代码中再加入一条 `@CheckRule`:

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

在这个表达式中，我们使用 `@` 符号来获取 Bean 并调用了校验方法，这是 SpEL 的机制，用户可自行查阅官方文档学习更多用法。

#### 4. 异常处理

如果一条 `@CheckRule` 校验失败，默认抛出  `CheckFailedException` 异常，这是在 `DefaultExceptionProvider`
声明的，使用者可自行实现 `ExceptionProvider` 接口，并注册为 Spring Bean，本框架可自动搜索注册到系统中，下面是简单的示例：

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

实际项目中还需要一个 `ExceptionHandler` 来捕获异常返回 400 参数校验错误:

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

1. 请勿注册多个 `ExceptionProvider` bean，多个 `ExceptionProvider` 产生歧义导致报错。
2. 自定义的异常必须是 `RuntimeException` 的子类(`ExceptionProvider` 泛型也做了限制)，这是由于本框架是基于 Spring AOP
   代理实现，在代理执行过程中，如果抛出未被声明在方法上的 `checked exception`
   会被代理捕获包装到 `UndeclaredThrowableException` 再外抛。

### Annotation

#### @Checkable

`@Checkable` 注解使用再方法上（通常是 Controller 方法），代表了此方法的参数需要校验，此外还提供了 `stopOnFirstFailure` (
default `false`) 选项用来标识是否在第一个校验失败的时候停止后续校验。

#### @CheckRule

`@CheckRule` 注解用于 `@Checkable` 注解内部，如同本文之前的例子，支持多个校验规则。每个规则有 `expression`, `message`,
及 `field` 三个属性。