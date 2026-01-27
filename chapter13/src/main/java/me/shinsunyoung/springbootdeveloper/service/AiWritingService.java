package me.shinsunyoung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.dto.WritingSuggestionDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AiWritingService {

    private final ChatClient chatClient;

    public WritingSuggestionDto getWritingSuggestion(String title, String content, String question) {
        BeanOutputConverter<WritingSuggestionDto> outputConverter =
                new BeanOutputConverter<>(WritingSuggestionDto.class);

        String prompt = buildPrompt(title, content, question, outputConverter.getFormat());

        String response = chatClient
                .prompt(prompt)
                .options(OpenAiChatOptions.builder()
                        .temperature(0.3)
                        .maxTokens(500)
                        .build())
                .call()
                .content();

        return outputConverter.convert(response);
    }

    private String buildPrompt(String title, String content, String question, String format) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("당신은 간결한 블로그 작성 도우미입니다.\n\n");

        if (title != null && !title.isBlank()) {
            promptBuilder.append("현재 작성 중인 글의 제목: ")
                    .append(title)
                    .append("\n\n");
        }

        if (content != null && !content.isBlank()) {
            promptBuilder.append("현재까지 작성된 내용:\n")
                    .append(content)
                    .append("\n\n");
        }

        promptBuilder.append("작성자의 질문: ")
                .append(question)
                .append("\n\n")
                .append("응답 규칙:\n")
                .append("- 3-5개의 간결한 아이디어만 제시 (각 1-2문장)\n")
                .append("- 불필요한 설명 제외\n\n")
                .append(format);

        return promptBuilder.toString();
    }
}
