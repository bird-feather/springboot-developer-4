package me.shinsunyoung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AiThumbnailService {

    private final ImageModel imageModel;
    private final ChatClient chatClient;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String generateThumbnail(String title, String content) {
        // 1단계: 글 내용을 바탕으로 이미지 프롬프트 생성
        String imagePromptText = generateImagePrompt(title, content);

        // 2단계: DALL-E로 이미지 생성
        ImagePrompt imagePrompt = new ImagePrompt(imagePromptText);

        ImageResponse response = imageModel.call(imagePrompt);

        // 생성된 이미지 URL (임시 URL)
        String tempImageUrl = response.getResult().getOutput().getUrl();

        // 3단계: 이미지를 다운로드하여 로컬에 저장
        try {
            return downloadAndSaveImage(tempImageUrl);
        } catch (IOException e) {
            // 다운로드 실패 시 원본 URL 반환
            return tempImageUrl;
        }
    }

    private String downloadAndSaveImage(String imageUrl) throws IOException {
        // 업로드 디렉토리를 절대 경로로 변환
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 생성
        String filename = "ai_thumbnail_" + UUID.randomUUID().toString() + ".png";
        Path filePath = uploadPath.resolve(filename);

        // 이미지 다운로드 및 저장
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 로컬 URL 반환
        return "/uploads/" + filename;
    }

    private String generateImagePrompt(String title, String content) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("당신은 블로그 썸네일을 위한 이미지 프롬프트를 생성하는 전문가입니다.\n\n");

        if (title != null && !title.isBlank()) {
            promptBuilder.append("블로그 제목: ").append(title).append("\n\n");
        }

        if (content != null && !content.isBlank()) {
            // 내용이 너무 길면 앞부분만 사용
            String shortContent = content.length() > 500 ? content.substring(0, 500) + "..." : content;
            promptBuilder.append("블로그 내용:\n").append(shortContent).append("\n\n");
        }

        promptBuilder.append("위 블로그 글의 내용을 시각적으로 표현할 수 있는 썸네일 이미지를 생성하기 위한 영어 프롬프트를 작성해주세요.\n");
        promptBuilder.append("응답은 오직 이미지 생성 프롬프트만 포함해야 하며, 다른 설명은 포함하지 마세요.\n");
        promptBuilder.append("프롬프트는 'A beautiful and clean illustration of ...' 형식으로 시작하고, ");
        promptBuilder.append("깔끔하고 모던한 스타일, 밝은 색상, 미니멀한 디자인을 강조해주세요.");

        String response = chatClient
                .prompt(promptBuilder.toString())
                .call()
                .content();

        return response.trim();
    }
}
