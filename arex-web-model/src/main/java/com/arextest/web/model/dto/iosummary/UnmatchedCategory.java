package com.arextest.web.model.dto.iosummary;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.DiffResultCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
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

    public static UnmatchedCategory computeCategory(CompareResultDto compareResult) {
        switch (compareResult.getDiffResultCode()) {
            case DiffResultCode.COMPARED_WITHOUT_DIFFERENCE:
                return UnmatchedCategory.MATCHED;
            case DiffResultCode.COMPARED_INTERNAL_EXCEPTION:
                return UnmatchedCategory.UNKNOWN;
            default: {
                List<LogEntity> entities = compareResult.getLogs();
                if (entities == null || entities.size() == 0) {
                    return UnmatchedCategory.UNKNOWN;
                } else if (entities.size() > 1) {
                    return UnmatchedCategory.VALUE_DIFF;
                }
                if (compareResult.getBaseMsg() == null) {
                    return UnmatchedCategory.LEFT_MISSING;
                } else if (compareResult.getTestMsg() == null) {
                    return UnmatchedCategory.RIGHT_MISSING;
                }
                return UnmatchedCategory.VALUE_DIFF;
            }
        }
    }

}

