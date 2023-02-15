package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.component.SendHelper;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.parse;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})\\s(\\W+)");

    private static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm");


    private TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;
    private final SendHelper sendHelper;

    public TelegramBotUpdatesListener (TelegramBot telegramBot,
                                       NotificationTaskService notificationTaskService,
                                       SendHelper sendHelper) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
        this.sendHelper = sendHelper;
    }



    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    @PostConstruct
    public void bot(){
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    sendHelper.sendMessage(chatId,
                            "Для планирования задачи отправте её в формате:\n**01.01.2023 20:00 Сделать домашнюю работу**",
                            ParseMode.MarkdownV2
                    );
                } else {
                    Matcher matcher = PATTERN.matcher(text);
                    LocalDateTime dateTime;
                    if (matcher.matches() && (dateTime = parse(matcher.group(1))) != null) {
                        String message = matcher.group(2);
                        notificationTaskService.create(chatId,message,dateTime);
                        sendHelper.sendMessage(chatId, "Заача апланирована!");
                    } else {
                        sendHelper.sendMessage(chatId, "Некорректный формат сообщения!");
                    }
                }
            });
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
     }


         @Nullable
         private LocalDateTime parse (String dateTime) {
             try {
                 return LocalDateTime.parse(dateTime, DATA_TIME_FORMATTER);
             } catch (DateTimeParseException e) {
                 return null;
             }
         }
}


