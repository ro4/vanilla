package me.ro4.vanilla.check;

import me.ro4.vanilla.Context;
import me.ro4.vanilla.constant.MagicMark;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultExceptionProvider implements ExceptionProvider<IllegalArgumentException> {

    @Override
    public IllegalArgumentException produce(List<Context> contexts) {
        List<String> messages = contexts.stream().map(p -> (String) p.getAttribute(MagicMark.MESSAGE))
                .collect(Collectors.toList());
        return new IllegalArgumentException(StringUtils.collectionToCommaDelimitedString(messages));
    }
}
