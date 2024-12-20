package io.github.giova333.userprofileservice.infrastructure.kafka.collect;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class SpelValueExtractor {

    SpelExpressionParser expressionParser = new SpelExpressionParser();
    MapAccessor accessor = new MapAccessor();

    public <T> T extractValue(Map<String, Object> data,
                              String path,
                              Class<T> type) {

        var context = new StandardEvaluationContext(data);
        context.addPropertyAccessor(accessor);

        return expressionParser.parseExpression(path).getValue(context, type);
    }

    public Object extractValue(Map<String, Object> data,
                               String path) {
        return extractValue(data, path, Object.class);
    }
}
