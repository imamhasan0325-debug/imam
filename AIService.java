package chatbot;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class AIService {
    private static final String API_URL = "https://router.huggingface.co/gpt2";

    private static final String API_KEY = "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"; // Replace safely

    public String getAIResponse(String message) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject input = new JSONObject();
            input.put("inputs", message);

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                input.toString()
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                int code = response.code();
                String responseBody = response.body() != null ? response.body().string() : "";

                System.out.println("DEBUG >> HTTP " + code + ": " + responseBody);

                if (!response.isSuccessful()) {
                    if (responseBody.contains("is currently loading"))
                        return "⏳ The AI model is warming up. Try again in a few seconds.";
                    return "⚠️ API Error (" + code + "): " + responseBody;
                }

                JSONArray arr = new JSONArray(responseBody);
                if (arr.length() > 0 && arr.getJSONObject(0).has("generated_text")) {
                    return arr.getJSONObject(0).getString("generated_text").trim();
                }

                return "Unexpected response: " + responseBody;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Error: " + e.getMessage();
        }
    }
}









