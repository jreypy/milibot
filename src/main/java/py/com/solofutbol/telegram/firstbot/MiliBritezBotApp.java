package py.com.solofutbol.telegram.firstbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class MiliBritezBotApp implements CommandLineRunner {
    public static final String HTTP_TOKEN = "866652928:AAH4H1jZk4TjcQ3-QwszNA0UFzdSepWraO4";

    public static void main(String[] args) {
        //Add this line to initialize bots context

        ApiContextInitializer.init();
        SpringApplication.run(MiliBritezBotApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
