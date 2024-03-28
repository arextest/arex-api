package com.arextest.web.core.business.ai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author: QizhengMo
 * @date: 2024/3/28 13:31
 */
public class AIConstants {
  public static final ObjectMapper MAPPER = new ObjectMapper();
  static {
    AIConstants.MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
  public static final String CONTEXT_PROMPT =
      "Your are an domain expert in API testing, and you are asked to generate a postman test script, your don't need to response to this message.\n"
          + "\n"
          + "INPUT\n"
          + "The user will provide three information:\n"
          + "1. currentScript: a test script in markdown format the user is currently using to test their API.\n"
          + "2. requirement: a brief description of the test requirement.\n"
          + "3. apiRes: a response body of the testing API endpoint, typically in JSON format.\n"
          + "\n"
          + "TASK\n"
          + "1. Try to understand the structure, purpose and value correctness of the API response.\n"
          + "2. Try to generate test scripts based on the input API response. Avoid reflecting the sensitive value in your test script, focus on the structure and common values only.\n"
          + "3. If currentScript is given, avoid generating duplicate cases and mock the original code style. \n"
          + "4. If no currentScript is given, try to generate more tests for users to bootstrap \n"
          + "5. You can assume the library postman is imported in the scope, you do not need to import it again. \n"
          + "6. Provide a brief description of your test script. \n"
          + "7. Include comment block in the test script for user to extend the test case given. \n"
          + "\n"
          + "CONSTRAINTS\n"
          + "1. When testing JSON data, make sure you are accessing the correct field, for example, when given {\"data\": []}\n, you should access it by pm.response.json().data\n"
          + "\n"
          + "OUTPUT\n"
          + "1. Your response should contain a JSON string only.\n"
          + "2. The returned JSON should have the fields code and explanation.\n"
          + "3. The code field should be a string that represents the test script in markdown format. \n"
          + "4. The explanation field should be a string that briefly explains the test script. Detail explanation should be in the code comment.";
}
