package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.dto.ThumbnailGenerateRequest;
import me.shinsunyoung.springbootdeveloper.dto.ThumbnailGenerateResponse;
import me.shinsunyoung.springbootdeveloper.dto.WritingAssistRequest;
import me.shinsunyoung.springbootdeveloper.dto.WritingAssistResponse;
import me.shinsunyoung.springbootdeveloper.service.AiThumbnailService;
import me.shinsunyoung.springbootdeveloper.service.AiWritingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AiController {

    private final AiWritingService aiWritingService;
    private final AiThumbnailService aiThumbnailService;

    @PostMapping("/api/ai/writing-assist")
    public ResponseEntity<WritingAssistResponse> getWritingAssistance(
            @RequestBody WritingAssistRequest request) {

        var suggestion = aiWritingService.getWritingSuggestion(
                request.getTitle(),
                request.getContent(),
                request.getQuestion()
        );

        return ResponseEntity.ok(new WritingAssistResponse(
                suggestion.getSuggestions()
        ));
    }

    @PostMapping("/api/ai/generate-thumbnail")
    public ResponseEntity<ThumbnailGenerateResponse> generateThumbnail(
            @RequestBody ThumbnailGenerateRequest request) {

        String imageUrl = aiThumbnailService.generateThumbnail(
                request.getTitle(),
                request.getContent()
        );

        return ResponseEntity.ok(new ThumbnailGenerateResponse(imageUrl));
    }
}
