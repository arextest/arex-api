package com.arextest.web.model.dto.iosummary;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public enum UnmatchedCategory {

    MATCHED((byte) 0),
    VALUE_DIFF((byte) 1),
    LEFT_MISSING((byte) 2),
    RIGHT_MISSING((byte) 4),
    UNKNOWN((byte) 8);

    private final static Map<Byte, UnmatchedCategory> CODE_VALUE_MAP = asMap(UnmatchedCategory::getCode);

    public static UnmatchedCategory of(Byte code) {
        return CODE_VALUE_MAP.get(code);
    }

    @Getter
    private final byte code;

    UnmatchedCategory(byte code) {
        this.code = code;
    }

    private static <K> Map<K, UnmatchedCategory> asMap(Function<UnmatchedCategory, K> keySelector) {
        UnmatchedCategory[] values = values();
        Map<K, UnmatchedCategory> mapResult = new HashMap<>(values.length);
        for (int i = 0; i < values.length; i++) {
            UnmatchedCategory category = values[i];
            mapResult.put(keySelector.apply(category), category);
        }
        return mapResult;
    }

}

