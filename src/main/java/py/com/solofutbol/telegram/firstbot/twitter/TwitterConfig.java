package py.com.solofutbol.telegram.firstbot.twitter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@EnableScheduling
public class TwitterConfig {
    @Value("${spring.social.twitter.appId}")
    private String consumerKey;
    @Value("${spring.social.twitter.appSecret}")
    private String consumerSecret;
    @Value("${twitter.access.token}")
    private String accessToken;
    @Value("${twitter.access.token.secret}")
    private String accessTokenSecret;



    @Bean
    public Twitter createTwitter() {

        TwitterTemplate twitterTemplate =
                new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);

        return twitterTemplate;
    }
}