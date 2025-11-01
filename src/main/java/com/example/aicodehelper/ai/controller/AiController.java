package com.example.aicodehelper.ai.controller;

import com.example.aicodehelper.ai.AiCodeHelperService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @GetMapping(value = "/chat")
    public SseEmitter chat(int memoryId, String message) {
        SseEmitter emitter = new SseEmitter();
        AtomicBoolean isFirst = new AtomicBoolean(true);

        aiCodeHelperService.chatStream(memoryId, message)
                .subscribe(
                        chunk -> {
                            try {
                                log.debug("Received chunk: [{}]", chunk);

                                if (chunk == null || chunk.trim().isEmpty()) {
                                    log.debug("Empty chunk, skipping");
                                    return;
                                }

                                String processedChunk = chunk;

                                // Add space before non-first, non-punctuation chunks
                                // But DON'T add space before Chinese/Japanese/Korean characters
                                if (!isFirst.get() && !chunk.startsWith(" ") &&
                                    !isPunctuation(chunk.charAt(0)) &&
                                    !isCJK(chunk.charAt(0))) {
                                    processedChunk = " " + chunk;
                                    log.debug("Added space: [{}] -> [{}]", chunk, processedChunk);
                                } else {
                                    log.debug("No space. isFirst={}, startsWithSpace={}, isPunctuation={}, isCJK={}",
                                            isFirst.get(), chunk.startsWith(" "),
                                            isPunctuation(chunk.charAt(0)), isCJK(chunk.charAt(0)));
                                }

                                isFirst.set(false);

                                // Send as SSE event with TEXT_PLAIN to preserve spaces
                                emitter.send(SseEmitter.event()
                                        .data(processedChunk, org.springframework.http.MediaType.TEXT_PLAIN));

                            } catch (IOException e) {
                                log.error("Error sending SSE event", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("Error in stream", error);
                            emitter.completeWithError(error);
                        },
                        () -> {
                            log.debug("Stream completed");
                            emitter.complete();
                        }
                );

        return emitter;
    }


    /**
     * Check if character is Chinese, Japanese, or Korean
     */
    private boolean isCJK(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == Character.UnicodeBlock.HIRAGANA
                || block == Character.UnicodeBlock.KATAKANA
                || block == Character.UnicodeBlock.HANGUL_SYLLABLES;
    }
    private boolean isPunctuation(char c) {
        String punctuations = ".,;:!?，。；：！？、\"\"''()[]{}《》【】";
        return punctuations.indexOf(c) != -1;
    }
}
