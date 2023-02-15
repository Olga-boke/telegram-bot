package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;


    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Transactional
    public void create (Long chatId,
                       String message,
                       LocalDateTime dateTime) {
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setUserId(chatId);
        notificationTask.setMessage(message);
        notificationTask.setNotificationDataTime(dateTime.truncatedTo(ChronoUnit.MINUTES));
        notificationTaskRepository.save(notificationTask);

    }


}

