package apgenticai.example.java.mcpclient.clients;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class StreamingPromptRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StreamingPromptRunner.class);

    private final ChatClient chatClient;

    public StreamingPromptRunner(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        this.chatClient = chatClientBuilder
                .defaultTools((mcpTools))
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Starting Streaming Prompt ---");

        String prompt = "Use your server tools Perform basic arithmetic operations on two numbers 1 and 2 with add operation";

        // .stream() returns a Project Reactor Flux of Strings
        Flux<String> responseStream = this.chatClient.prompt()
                .user(prompt)
                .stream()
                .content();

        System.out.println("\n--- Streaming Local LLM Response ---");

        // Subscribe to the stream and print tokens immediately as they arrive
        responseStream.doOnNext(token -> {
                    System.out.print(token);
                    System.out.flush();
                })
                .doOnComplete(() -> System.out.println("\n--- Stream Finished ---"))
                .blockLast(); // Keeps the runner thread alive until streaming is done
    }
}
