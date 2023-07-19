package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.model.CodeHostType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Qzmo on 2023/7/18
 */
@Component
public class ProviderFactory {
    @Resource
    List<JavaCodeContentProvider> providers;

    public JavaCodeContentProvider pick(CodeHostType type) {
        for (JavaCodeContentProvider provider : providers) {
            if (provider.getCodeHostType().equals(type)) {
                return provider;
            }
        }
        return null;
    }

}
