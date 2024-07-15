package com.example.chatgptdemo.aws.model.logic;

import com.example.chatgptdemo.aws.model.ChatRequest;
import com.example.chatgptdemo.aws.model.Message;
import com.example.chatgptdemo.aws.model.elasticSearch.model.QueryDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OpenAIService {

    private final WebClient webClient;
    private final String apiKey;

    private final String url = "https://api.openai.com/v1/chat/completions";

    public OpenAIService(WebClient.Builder webClientBuilder, @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
    }



    private String analyzerQuestion(String questionPrompt) {
        String analyzerPrompt = "{\n" +
                "  \"model\": \"gpt-4\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"You are a helpful assistant.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"我的資料庫有以下的類似資訊，格式如下：{\\\"product_info\\\": {\\\"id\\\": \\\"115\\\", \\\"system_id\\\": \\\"HPCHI2\\\", \\\"title\\\": \\\"南山人壽全心溢靠2醫療保險(HPCHI2)\\\", \\\"product_description\\\": \\\"提供住院、手術及重大疾病三合一保障\\\", \\\"product_feature\\\": \\\"\\\", \\\"age\\\": \\\"10年期:0歲~60歲/20年期:0歲~50歲\\\", \\\"payment_method\\\": \\\"年繳/半年繳/季繳/月繳\\\", \\\"payment_period\\\": \\\"10年/20年\\\", \\\"currency\\\": \\\"新台幣\\\", \\\"main_list\\\": [\\\"主約\\\"], \\\"assure_list\\\": [\\\"滿期保險金\\\",\\\"身故\\\",\\\"重大疾病\\\",\\\"住院日額\\\",\\\"門診手術\\\",\\\"住院手術\\\",\\\"豁免保險費\\\",\\\"外溢回饋\\\"], \\\"tag_list\\\": [\\\"新台幣\\\"], \\\"detail_url\\\": {\\\"url\\\": \\\"/product/103/115\\\", \\\"url_blank\\\": false, \\\"url_type\\\": \\\"INSIDE\\\"}}}\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"你可以幫我把以下這段話" + questionPrompt + "，依據上面的類型，分析出可以歸類在age、payment_method、payment_period、assure_list的屬性嗎，如果沒有就填入空即可，回傳格式為age=value,payment_method=value，不用額外說明文字\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        log.info("Request body: {}", analyzerPrompt);
        return analyzerPrompt;
    }

    private String createRequestBody(String prompt) {
        String formatJson = "{\n" +
                "    \"model\": \"gpt-4\",\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"role\": \"system\",\n" +
                "        \"content\": \"你是一個南山人壽的保險客服\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"role\": \"user\",\n" +
                "        \"content\":請幫我用適當的方生成以下內容：" + prompt + "\n" +
                "      }\n" +
                "    ]\n" +
                "  }";
        log.info("Request body: {}", formatJson);
        return formatJson;
    }


    public QueryDetails generateQueryByString(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        Message systemMessage = new Message();
        systemMessage.setRole("system");
        systemMessage.setContent("You are a helpful assistant.");
        Message userMessage = new Message();
        userMessage.setRole("user");
        userMessage.setContent("我保險商品有以下幾種屬性，標題：這是記載保險商品的名稱，通常會是南山人壽開頭，例如：南山人壽照得住長期照顧終身保險或是南山人壽新定期壽險附約；年齡：記載被保險人的年齡；繳費方式：會有年繳/半年繳/季繳/月繳四種方式；繳費期間：為繳費的時間，可能為1年、10年、20年、30年；特色：通常是這個保險的特色，會有身故、失能、滿期保險金、意外、住院、門診、癌症、回饋等等；種類：分為醫療、旅行、壽險、長期照護、年金");
        Message userMessage2 = new Message();
        userMessage2.setRole("user");
        userMessage2.setContent("你可以幫我把以下這段話\\" + prompt + "\"，依據上面的類型，分析出可以歸類在標題、年齡、繳費方式、繳費期間、特色、類型的屬性嗎，如果沒有就填入空即可，回傳格式為標題=value,年齡=value，不用額外說明文字");
        chatRequest.setMessages(List.of(new Message[]{systemMessage, userMessage, userMessage2}));
        System.out.println("使用者問題: " + prompt);

        // 封裝請求數據和頭部到HttpEntity
        HttpEntity<ChatRequest> requestEntity = new HttpEntity<>(chatRequest, headers);

        // 發送POST請求
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String chatGTPresponse = response.toString();
        log.info("ChatGTP Response: {}", chatGTPresponse);

        QueryDetails details = new QueryDetails();
        String content = "";
        try {
            // 創建ObjectMapper實例來處理JSON
            ObjectMapper mapper = new ObjectMapper();

            // 解析JSON響應體
            JsonNode rootNode = mapper.readTree(response.getBody());

            // 獲取choices數組的第一個元素
            JsonNode firstChoice = rootNode.path("choices").get(0);

            // 從第一個選擇中獲取message對象
            JsonNode messageNode = firstChoice.path("message");

            // 從message對象中獲取content屬性
            content = messageNode.path("content").asText();
            log.info("Content: {}", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String changeChatGTPresponse = content
                .replace("年齡", "age")
                .replace("繳費方式", "payment_method")
                .replace("繳費期間", "payment_period")
                .replace("特色", "insuranceFeature")
                .replace("標題", "title")
                .replace("類型", "insuranceType");

        String[] parts = changeChatGTPresponse.split(",");
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                switch (key) {
                    case "age":
                        log.info("setAge: {}", value);
                        details.setAge(Optional.of(value));
                        break;
                    case "payment_method":
                        log.info("setPaymentMethod: {}", value);
                        details.setPaymentMethod(Optional.of(value));
                        break;
                    case "payment_period":
                        log.info("setPaymentPeriod: {}", value);
                        details.setPaymentPeriod(Optional.of(value));
                        break;
                    case "assure_list":
                        log.info("setAssureList: {}", value);
                        details.setAssureList(Optional.of(value));
                        break;
                    default:
                        log.warn("Unknown key: {}", key);
                }
            }
        }
        return details;
    }


    public String generateResponseByString(String QueryResult, String customerMessage) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        Message systemMessage = new Message();
        systemMessage.setRole("system");
        systemMessage.setContent("你是一個南山人壽的保險客服");
        Message userMessage = new Message();
        userMessage.setRole("user");
        userMessage.setContent("如果有人問了以下的問題：" + customerMessage + "，請幫我用這個內容：" + QueryResult + "，生成一個適當的回答。");
        chatRequest.setMessages(List.of(new Message[]{systemMessage, userMessage}));

        // 封裝請求數據和頭部到HttpEntity
        HttpEntity<ChatRequest> requestEntity = new HttpEntity<>(chatRequest, headers);

        // 發送POST請求
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String content = response.toString();
        System.out.println("ChatGTP Response: " + content);
        log.info("ChatGTP Response: {}", content);

        return content;
    }

}