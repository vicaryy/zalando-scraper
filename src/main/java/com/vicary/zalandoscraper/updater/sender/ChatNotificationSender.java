package com.vicary.zalandoscraper.updater.sender;

import com.vicary.zalandoscraper.model.ChatNotification;
import com.vicary.zalandoscraper.service.repository_services.NotificationChatService;
import com.vicary.zalandoscraper.api_telegram.service.QuickSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ChatNotificationSender {

    private final static Logger logger = LoggerFactory.getLogger(ChatNotificationSender.class);
    private int messagesSent;
    private int messagesFailed;
    private final NotificationChatService notificationChatService;
    private final QuickSender quickSender;

    public ChatNotificationSender(NotificationChatService notificationChatService, QuickSender quickSender) {
        this.notificationChatService = notificationChatService;
        this.quickSender = quickSender;
    }

    public void send(List<ChatNotification> notifications) {
        messagesSent = 0;
        messagesFailed = 0;
        notifications.forEach(n -> send(n, false));
    }

    public void sendAndSave(List<ChatNotification> notifications) {
        messagesSent = 0;
        messagesFailed = 0;
        notifications.forEach(n -> send(n, true));
    }

    private void send(ChatNotification notification, boolean save) {
        try {
            quickSender.notification(notification);
            messagesSent++;
            if (save)
                saveToRepository(notification);
        } catch (Exception e) {
            logger.warn("[Chat Notification Sender] Failed in sending chat notification to '{}'", notification.getChatId());
            logger.warn("[Chat Notification Sender] {}", e.getMessage());
            messagesFailed++;
        }
    }

    private void saveToRepository(ChatNotification notification) {
        notificationChatService.saveEntity(notification);
    }

    public int getSentAmountAndReset() {
        int amount = messagesSent;
        messagesSent = 0;
        return amount;
    }

    public int getFailedAmount() {
        return messagesFailed;
    }

    public int getFailedAmountAndReset() {
        int amount = messagesFailed;
        messagesFailed = 0;
        return amount;
    }
}
