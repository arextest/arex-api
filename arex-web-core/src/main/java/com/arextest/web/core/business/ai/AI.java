package com.arextest.web.core.business.ai;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */

import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AI {
  private static final String project = "trip-flt-bi-dbprj";
  private static final String location = "asia-northeast1";
  private static final String publisher = "google";
  private static final String model = "text-bison@001";


  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    // Details of designing text prompts for supported large language models:
    // https://cloud.google.com/vertex-ai/docs/generative-ai/text/text-overview
    String instance =
        "{ \"prompt\": " + "\"Give me ten interview questions for the role of program manager.\"}";
    String parameters =
        "{\n"
            + "  \"temperature\": 0.2,\n"
            + "  \"maxOutputTokens\": 256,\n"
            + "  \"topP\": 0.95,\n"
            + "  \"topK\": 40\n"
            + "}";


    predictTextPrompt(instance, parameters, project, location, publisher, model);
  }

  // Get a text prompt from a supported text model
  public static void predictTextPrompt(
      String instance,
      String parameters,
      String project,
      String location,
      String publisher,
      String model)
      throws IOException {
    String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
    PredictionServiceSettings predictionServiceSettings =
        PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    try (PredictionServiceClient predictionServiceClient =
        PredictionServiceClient.create(predictionServiceSettings)) {
      final EndpointName endpointName =
          EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      Value.Builder instanceValue = Value.newBuilder();
      JsonFormat.parser().merge(instance, instanceValue);
      List<Value> instances = new ArrayList<>();
      instances.add(instanceValue.build());

      // Use Value.Builder to convert instance to a dynamically typed value that can be
      // processed by the service.
      Value.Builder parameterValueBuilder = Value.newBuilder();
      JsonFormat.parser().merge(parameters, parameterValueBuilder);
      Value parameterValue = parameterValueBuilder.build();

      PredictResponse predictResponse =
          predictionServiceClient.predict(endpointName, instances, parameterValue);
      System.out.println("Predict Response");
      System.out.println(predictResponse);
    }
  }
}
