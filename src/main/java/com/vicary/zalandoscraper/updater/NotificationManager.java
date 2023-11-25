package com.vicary.zalandoscraper.updater;

import com.vicary.zalandoscraper.entity.WaitingUserEntity;
import com.vicary.zalandoscraper.model.Email;
import com.vicary.zalandoscraper.model.ChatNotification;
import com.vicary.zalandoscraper.service.dto.ProductDTO;
import com.vicary.zalandoscraper.service.repository_services.ProductService;
import com.vicary.zalandoscraper.service.repository_services.UserService;
import com.vicary.zalandoscraper.service.repository_services.WaitingUserService;
import com.vicary.zalandoscraper.updater.sender.EmailNotificationSender;
import com.vicary.zalandoscraper.updater.sender.ChatNotificationSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationManager {

    private final static Logger logger = LoggerFactory.getLogger(NotificationManager.class);

    private final ProductService productService;

    private final ChatNotificationSender chatSender;

    private final EmailNotificationSender emailSender;

    private final WaitingUserService waitingUserService;

    private final UserService userService;

    public void send(List<ProductDTO> DTOs) {
        sendPriceNotifications(DTOs);
        sendWaitingUserNotifications();
    }

    private void sendPriceNotifications(List<ProductDTO> DTOs) {
        List<ProductDTO> listThatNeedsToBeSend = DTOs.stream()
                .filter(this::isUserNeedsNotify)
                .toList();
        if (listThatNeedsToBeSend.isEmpty()) {
            logger.info("[Notification Manager] No price notifications to send.");
            return;
        }

        listThatNeedsToBeSend.forEach(this::updatePriceAlertInRepository);

        List<ChatNotification> chatNotifications = getChatNotifications(listThatNeedsToBeSend);
        List<Email> emails = getEmailNotifications(listThatNeedsToBeSend);

        displayLogsBeforeSendPriceNotifications(chatNotifications.size(), emails.size());

        chatSender.sendAndSave(chatNotifications);
        emailSender.sendAndSave(emails);

        displayLogsAfterSendPriceNotifications();
    }

    private void sendWaitingUserNotifications() {
        List<ChatNotification> waitingUserNotifications = getWaitingUserNotifications();
        if (waitingUserNotifications.isEmpty()) {
            logger.info("[Notification Manager] No waiting user notifications to send.");
            return;
        }

        displayLogsBeforeSendWaitingUser(waitingUserNotifications.size());
        chatSender.send(waitingUserNotifications);
        displayLogsAfterSendWaitingUser();
    }

    private boolean isUserNeedsNotify(ProductDTO p) {
        if (p.getPriceAlert().equals("OFF") || p.getNewPrice() == 0)
            return false;

        if (p.getPriceAlert().equals("AUTO"))
            return p.getNewPrice() < p.getPrice();

        double priceAlert = Double.parseDouble(p.getPriceAlert());

        return p.getNewPrice() <= priceAlert;
    }

    private void updatePriceAlertInRepository(ProductDTO p) {
        if (p.getPriceAlert().equals("OFF") || p.getPriceAlert().equals("AUTO"))
            return;

        double priceAlert = Double.parseDouble(p.getPriceAlert());
        if (p.getNewPrice() <= priceAlert)
            productService.updateProductPriceAlert(p.getProductId(), "OFF");
    }

    private List<ChatNotification> getChatNotifications(List<ProductDTO> DTOs) {
        List<ChatNotification> chatNotifications = new ArrayList<>();
        for (ProductDTO p : DTOs) {
            ChatNotification notification = new ChatNotification();
            notification.setChatId(p.getUserId());
            notification.setMarkdownV2(true);
            notification.setDefaultPriceAlertMessage(p);
            chatNotifications.add(notification);
        }
        return chatNotifications.isEmpty() ? Collections.emptyList() : chatNotifications;
    }

    private List<Email> getEmailNotifications(List<ProductDTO> DTOs) {
        List<Email> emails = new ArrayList<>();
        for (ProductDTO p : DTOs) {
            if (p.isNotifyByEmail()) {
                Email notification = new Email(p.getEmail());
                notification.setPriceAlertMessageAndTitle(p);
                emails.add(notification);
            }
        }
        return emails.isEmpty() ? Collections.emptyList() : emails;
    }

    private List<ChatNotification> getWaitingUserNotifications() {
        List<WaitingUserEntity> waitingUserEntities = waitingUserService.getAllAndDeleteWaitingUsers();
        if (waitingUserEntities.isEmpty())
            return Collections.emptyList();

        List<ChatNotification> chatNotifications = new ArrayList<>();
        for (WaitingUserEntity w : waitingUserEntities) {
            ChatNotification notification = new ChatNotification();
            notification.setChatId(w.getUserId());
            notification.setWaitingUserMessage(userService.getLanguageByUserId(w.getUserId()));
            notification.setMarkdownV2(true);
            chatNotifications.add(notification);
        }
        return chatNotifications;
    }

    private void displayLogsBeforeSendWaitingUser(int waitingUserSize) {
        logger.info("[Notification Manager] Sending {} waiting user notifications.", waitingUserSize);
    }

    private void displayLogsAfterSendWaitingUser() {
        logger.info("[Notification Manager] Successfully sent {} waiting user notifications.", chatSender.getSentAmountAndReset());

        if (chatSender.getFailedAmount() != 0)
            logger.info("[Notification Manager] Failed {} in sending waiting user notifications.", chatSender.getFailedAmountAndReset());
    }

    private void displayLogsBeforeSendPriceNotifications(int chatNotifiesSize, int emailNotifiesSize) {
        logger.info("[Notification Manager] Sending {} chat notifications.", chatNotifiesSize);
        logger.info("[Notification Manager] Sending {} email notifications.", emailNotifiesSize);
    }

    private void displayLogsAfterSendPriceNotifications() {
        logger.info("[Notification Manager] Successfully sent {} chat notifications.", chatSender.getSentAmountAndReset());
        logger.info("[Notification Manager] Successfully sent {} email notifications.", emailSender.getSentAmountAndReset());

        if (chatSender.getFailedAmount() != 0)
            logger.info("[Notification Manager] Failed {} in sending chat notifications.", chatSender.getFailedAmountAndReset());

        if (emailSender.getFailedAmount() != 0)
            logger.info("[Notification Manager] Failed {} in sending email notifications.", emailSender.getFailedAmountAndReset());
    }
}
