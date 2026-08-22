package dio.budgeting.infrastructure.ai;

import org.springframework.ai.audio.speech.SpeechPrompt;
import org.springframework.ai.audio.speech.SpeechResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionModel;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.speech.SpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Entry point for the voice-driven flow described in the project brief:
 * 1) receive an audio file, 2) transcribe it to text, 3) let the AI model
 * pick and call the right tool, 4) convert the final answer back to audio.
 */
@RestController
@RequestMapping("/api/assistant")
public class VoiceAssistantController {

    private final AudioTranscriptionModel transcriptionModel;
    private final SpeechModel speechModel;
    private final ChatClient chatClient;

    public VoiceAssistantController(AudioTranscriptionModel transcriptionModel,
                                     SpeechModel speechModel,
                                     ChatClient chatClient) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
        this.chatClient = chatClient;
    }

    /**
     * Full voice flow: uploads an audio command and gets an audio (mp3) answer back.
     */
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> handleVoiceCommand(@RequestParam("audio") MultipartFile audio) {
        String transcript = transcribe(audio);
        String answer = chatClient.prompt()
                .user(transcript)
                .call()
                .content();

        byte[] speech = synthesize(answer);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(speech);
    }

    /**
     * Text-only variant of the same flow, handy for debugging without recording audio.
     */
    @PostMapping(value = "/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleTextCommand(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    private String transcribe(MultipartFile audio) {
        try {
            var resource = new ByteArrayResource(audio.getBytes()) {
                @Override
                public String getFilename() {
                    return audio.getOriginalFilename() != null ? audio.getOriginalFilename() : "audio.wav";
                }
            };
            var response = transcriptionModel.call(new AudioTranscriptionPrompt(resource));
            return response.getResult().getOutput();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded audio file", e);
        }
    }

    private byte[] synthesize(String text) {
        SpeechResponse response = speechModel.call(new SpeechPrompt(text));
        return response.getResult().getOutput();
    }
}
