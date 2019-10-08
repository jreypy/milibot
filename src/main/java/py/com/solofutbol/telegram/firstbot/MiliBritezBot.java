package py.com.solofutbol.telegram.firstbot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class MiliBritezBot extends TelegramLongPollingBot {
    Log log = LogFactory.getLog(MiliBritezBot.class);

    String REGEX = "/mili[^a-zA-Z]*\\s*.*";


    @Autowired
    private Twitter twitter;

    @Value("${telegram.token}")
    String HTTP_TOKEN = "";

    long lastUpdate = 0;
    Long record = null;
    Long hit = null;
    Iterator<Tweet> tweetList = null;
    Long chatId = null;

    int index = 0;
    long[] ids = Setup.IDS;
    String[] names = Setup.NAMES;
    Long[] records = Setup.RECORDS;
    Boolean[] last = Setup.LAST;
    Long lastMessage = null;

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        log.info("-> " + updates);
        super.onUpdatesReceived(updates);
    }

    public void onUpdateReceived(Update update) {
        long now = System.currentTimeMillis();
        lastMessage = now;
        log.info(lastMessage);
        chatId = update.getMessage().getChatId();

        // Si es mili... responder con algo
        String command = update.getMessage().getText();
        if (command.matches(REGEX)) {
            String[] strings = command.split("\\s");

            int newIndex = -1;

            if (strings.length > 1) {
                String string = strings[1];
                log.info("buscar->[" + strings[1] + "]");
                if (string != null) {
                    newIndex = Arrays.asList(names).indexOf(org.apache.commons.lang3.StringUtils.stripAccents(StringUtils.capitalize(string.trim().toLowerCase())));
                }
                log.info("new index is [" + newIndex + "]");
            }

            if (newIndex >= 0) {
                index = newIndex;
                tweetList = null;
                sendMessage(false);
            } else {
                sendMessage(true);
            }

        }
    }

    @Scheduled(fixedDelay = 600000)
    public void scheduleFixedDelayTask() {
        log.info(
                " Scheduled Event" + System.currentTimeMillis() / 1000);
        long now = System.currentTimeMillis();

        if (lastMessage == null) {
            // esperar a que venga un mensaje
            log.info(" No hay nada que informar");
            return;
        }

        if (now - lastMessage > 3600000) {
            sendMessage(true);
//            log.info("Enviar un mensaje al grupo que está muy tranquilo");
            lastMessage = null;
        } else {
            log.info("Hay que esperar mas...");
        }
    }

    private void sendMessage(boolean random) {
        // Si no tengo Chat Id, no se a quien responder
        if (chatId == null)
            return;


        long now = System.currentTimeMillis();

        // Descargar tuits cada 5 mins
        if (tweetList == null || !tweetList.hasNext() || (now - lastUpdate) > 300000) {
            if (random)
                index = (int) (Math.random() * ids.length);

            TimelineOperations timelineOperations = twitter.timelineOperations();
            record = null;
            // Descargar el tuit de index
            List<Tweet> tweetList = timelineOperations.getUserTimeline(ids[index], 3);
            this.tweetList = tweetList.listIterator();
            lastUpdate = now;
            last[index] = true;
        }

        if (tweetList.hasNext()) {
            Tweet tweet = tweetList.next();
            long tweetTime = tweet.getCreatedAt().getTime();
            String message = tweet.getFromUser() + " dice " + tweet.getText() + " ";
            // Si es el ultimo, no tengo record anterior o este record es superior al anterior
            record = now - tweetTime;
            if (last[index]) {
                int showTweetRule = (int) (Math.random() * 3);

                if (showTweetRule == 1) {
                    log.info("Mostrar el Tuit");
                } else if (record < 600000) {
                    message = "Ayudemos a " + names[index] + " que tuvo una recaida... " + message;
                } else if ((now - tweetTime) > 7200000 && (records[index] == null || (now - tweetTime) > records[index])) {
                    records[index] = record;
                    // Ya no es el ultimo tuit
                    last[index] = false;
                    message = "Un aplauso para " + names[index] + " que lleva " + (now - tweetTime) / 1000 / 60 + " minutos sin tuitear";
                }
            }
            log.info("Send Message [" + message + "]");
            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "MiliBritezBot";
    }

    public String getBotToken() {
        return HTTP_TOKEN;
    }


    //Bot body.
}