package com.arextest.web.core.business.ai;

import com.arextest.web.model.contract.contracts.ai.TestScriptGenRes;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author: QizhengMo
 * @date: 2024/3/28 13:31
 */
public class AIConstants {
  public static final ObjectMapper MAPPER = new ObjectMapper();
  static {
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
  }

  public static final TestScriptGenRes GENERAL_INVALID_RES = new TestScriptGenRes();
  static {
    GENERAL_INVALID_RES.setExplanation("Sorry, I can't generate test scripts for you now. Please try again later.");
  }

  public static final String CONTEXT_PROMPT =
      "Your are an domain expert in API testing, and you are asked to generate a postman test script, your don't need to response to this message.\n"
          + "\n"
          + "Input\n"
          + "The user will provide three information:\n"
          + "1. requirement: a brief description of the test requirement.\n"
          + "2. apiRes: a response body of the testing API endpoint, typically in JSON format.\n"
          + "\n"
          + "Task\n"
          + "1. Try to understand the structure, purpose and value correctness of the API response.\n"
          + "2. Try to generate test scripts based on the input API response. Avoid reflecting the sensitive value in your test script, focus on the structure and common values only.\n"
          + "3. Provide a brief description of your test script. \n"
          + "\n"
          + "Script Criteria\n"
          + "1. You can assume the library postman is imported in the scope, you do not need to import it again. \n"
          + "2. When testing JSON data, make sure you are accessing the correct field, for example, when given {\"data\": []}\n, you should access it by pm.response.json().data\n"
          + "\n"
          + "Output Format\n"
          + "1. Your response should be a valid JSON string, no markdown directive needed.\n"
          + "2. The returned JSON should have the fields code and explanation.\n"
          + "3. The code field should be a string that represents the test script in markdown format. \n"
          + "4. The explanation field should be a string that briefly explains the test script. Detail explanation should be in the code comment. \n"
          + "5. Using this JSON schema: Response = {\"code\": str, \"explanation\": str} ";

  public static final String USER_Q_1 = "{\n"
      + "    \"apiRes\": \"{\\\"code\\\": 200}\",\n"
      + "    \"requirement\": \"Test if the response is valid\"\n"
      + "}";

  public static final String AI_A_1 = "{\"code\": \"pm.test(\"Status code is 200\", function () {\n"
      + "    pm.response.to.have.status(200);\n"
      + "});\", \"explanation\": \"To verify the status code is 200\"}";

  public static final String SAFE_RES_REQUIREMENT = "Please provide a smaller snippet from now on to ensure you not exceeding the token limit.";

  public static final String FIX_SCRIPT_PROMPT =
      "Your are an domain expert in API testing, and you are asked to fix the given postman test script. "
      + "Input"
      + "The user will provide a broken postman test script."
      + "\n"
      + "Task\n"
      + "1. Locate grammar issues and fix them. \n"
      + "2. Locate logical issues and fix them. \n"
      + "\n"
      + "Output"
      + "1. Generate a fixed version of the old script. \n"
      + "2. Your output should use this JSON schema: Response = {\"code\": str}  \n";
}
