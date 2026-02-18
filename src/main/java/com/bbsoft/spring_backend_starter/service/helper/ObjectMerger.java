package com.bbsoft.spring_backend_starter.service.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Builder
public class ObjectMerger {

    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public <T> T shallowMerge(T target, T source, Class<T> cls, boolean copyNulls) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            return target;
        }
        final Map<String, Object> sourceFields = objectMapper.convertValue(source, Map.class);
        final Map<String, Object> targetFields = objectMapper.convertValue(target, Map.class);
        for (Map.Entry<String, Object> field : sourceFields.entrySet()) {
            if (Objects.nonNull(field.getValue()) || copyNulls) {
                targetFields.put(field.getKey(), field.getValue());
            }
        }
        return objectMapper.convertValue(targetFields, cls);
    }
}
