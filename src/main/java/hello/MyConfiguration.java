package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

@Configuration
@EnableIntegration
public class MyConfiguration {

    @Bean
    public FeedEntryMessageSource feedMessageSource() {
        try {
            return new FeedEntryMessageSource(
                    URI.create("http://spring.io/blog.atom").toURL(), ""
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public FileWritingMessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("/tmp/si"));
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setCharset("UTF-8");
        DefaultFileNameGenerator fileNameGenerator = new DefaultFileNameGenerator();
        fileNameGenerator.setExpression("'SpringBlog'");
        handler.setFileNameGenerator(fileNameGenerator);
        handler.setExpectReply(false);
        return handler;
    }

    @Bean
    public IntegrationFlow newsPollingFlow() {
        return IntegrationFlows.from(feedMessageSource(),
                c -> c.poller(Pollers.fixedRate(5000)))
                .transform("payload.title + ' @ ' + payload.link + \"\n\"")
                .handle(fileWritingMessageHandler())
                .get();
    }

}
