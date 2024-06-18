package com.arextest.web.core.business.ai.impls.aws.claude;

import com.arextest.web.core.business.ai.AIConstants;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.ai.FixReq;
import com.arextest.web.model.contract.contracts.ai.GenReq;
import com.arextest.web.model.contract.contracts.ai.ModelInfo;
import com.arextest.web.model.contract.contracts.ai.TestScriptFixRes;
import com.arextest.web.model.contract.contracts.ai.TestScriptGenRes;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;

/**
 * @author: QizhengMo
 * @date: 2024/6/17 20:36
 * @reference <a href="https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/bedrock-runtime/src/main/java/com/example/bedrockr">Bedrock Code Example</a>
 */
@Slf4j
public abstract class ClaudeProvider implements AIProvider {
  private final BedrockRuntimeClient bedrockClient;
  private static final ModelInfo modelInfo = new ModelInfo();
  private static final String ROLE_USER = "user";
  private static final String ROLE_ASSISTANT = "assistant";

  public ClaudeProvider(ClaudeConfig config) {
    bedrockClient = BedrockRuntimeClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.of(config.getRegion()))
        .build();
    modelInfo.setModelName(config.getModelId());
  }

  @Override
  public TestScriptGenRes generateScripts(GenReq genReq) {
    try {
      ConverseRequest req = ConverseRequest.builder()
          .modelId(modelInfo.getModelName())
          .system(SystemContentBlock.builder().text(AIConstants.CONTEXT_PROMPT).build())
          .messages(Lists.newArrayList(
              Message.builder()
                  .content(ContentBlock.builder().text(AIConstants.USER_Q_1).build())
                  .role(ROLE_USER)
                  .build(),
              Message.builder()
                  .content(ContentBlock.builder().text(AIConstants.AI_A_1).build())
                  .role(ROLE_ASSISTANT)
                  .build(),
              Message.builder()
                  .content(ContentBlock.builder().text(AIConstants.MAPPER.writeValueAsString(genReq)).build())
                  .role(ROLE_USER)
                  .build())
          )
          .build();
      ConverseResponse res = this.bedrockClient.converse(req);
      return new TestScriptGenRes();
    } catch (Exception e) {
      LOGGER.error("Failed to generate scripts: {}", e.getMessage(), e);
    }

    return null;
  }

  @Override
  public TestScriptFixRes fixScript(FixReq fixReq) {
    return null;
  }

  @Override
  public @NonNull ModelInfo getModelInfo() {
    return modelInfo;
  }
}
