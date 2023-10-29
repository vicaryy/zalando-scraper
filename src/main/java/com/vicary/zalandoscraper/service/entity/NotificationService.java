package com.vicary.zalandoscraper.service.entity;

import com.vicary.zalandoscraper.entity.NotificationEntity;
import com.vicary.zalandoscraper.repository.NotificationRepository;
import com.vicary.zalandoscraper.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationSender notificationSender;

    private final NotificationRepository repository;

    private final ProductService productService;


    public void saveNotification(NotificationEntity notification) {
        if (isUserNeedsToBeNotify(notification))
            repository.save(notification);
    }


    public void saveNotifications(List<NotificationEntity> notificationEntities) {
        int savedEntities = 0;
        for (NotificationEntity n : notificationEntities)
            if (isUserNeedsToBeNotify(n)) {
                repository.save(n);
                savedEntities++;
            }

        logger.info("Added {} notifications to database.", savedEntities);
    }


    private boolean isUserNeedsToBeNotify(NotificationEntity n) {
        if (n.getPriceAlert().equals("0"))
            return false;

        if (n.getPriceAlert().equals("AUTO"))
            return n.getNewPrice() != n.getOldPrice() && n.getNewPrice() < n.getOldPrice();

        double priceAlert = Double.parseDouble(n.getPriceAlert());

        if (n.getNewPrice() <= priceAlert) {
            productService.updateProductPriceAlert(n.getId(), "0");
            return true;
        }
        return false;
    }


    public void sendNotificationsToUsers() {
        List<NotificationEntity> notifications = repository.findAll();
        notificationSender.send(notifications);
        deleteAllNotifications();

//        logger.info("Sent and deleted {} notifications.", notifications.size());
    }

    public void deleteAllNotifications() {
        repository.deleteAll();
    }
}






















