package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Card;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.CardRepository;
import com.netbanking.netbanking_app.service.CardService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    private final Random random = new Random();

    @Override
    public Card addCard(Card card) {
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setStatus("ACTIVE");

        if (card.getDailyLimit() == null) {
            card.setDailyLimit(50000.00);
        }

        notificationService.triggerSqlNotification(
                card.getUser().getUserId(),
                "New card ending with " + card.getCardNumber().substring(card.getCardNumber().length() - 4) + " has been added to your account"
        );


        Card savedCard = cardRepository.save(card);

        try {
            User user = userService.getUserById(savedCard.getUserId());
            auditLogService.logAction(
                    "CARD_CREATED",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "Card ending in " + savedCard.getCardNumber().substring(savedCard.getCardNumber().length() - 4) +
                            " added for user " + user.getUsername(),
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (CARD_CREATED): " + e.getMessage());
        }

        return savedCard;
    }

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll(); // Global access — no audit
    }

    @Override
    public void updateCardStatus(Long cardId, String newStatus) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card != null) {
            card.setStatus(newStatus);
            card.setUpdatedAt(LocalDateTime.now());
            cardRepository.save(card);

            try {
                User user = userService.getUserById(card.getUserId());
                auditLogService.logAction(
                        "CARD_STATUS_UPDATE",
                        user.getUsername(),
                        user.getUserId(),
                        user.getUsername(),
                        "Card status updated to " + newStatus + " for card ID " + cardId,
                        false,
                        null
                );
            } catch (Exception e) {
                System.out.println("❌ Audit log error (CARD_STATUS_UPDATE): " + e.getMessage());
            }
        }
    }

    @Override
    public List<Card> getCardsByStatus(String status) {
        return cardRepository.findByStatusIgnoreCase(status);
    }

    @Override
    public Card getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);

        if (card != null) {
            try {
                User user = userService.getUserById(card.getUserId());
                auditLogService.logAction(
                        "CARD_FETCHED",
                        user.getUsername(),
                        user.getUserId(),
                        user.getUsername(),
                        "Accessed card ID " + cardId + " ending in " +
                                card.getCardNumber().substring(card.getCardNumber().length() - 4),
                        false,
                        null
                );
            } catch (Exception e) {
                System.out.println("❌ Audit log error (CARD_FETCHED): " + e.getMessage());
            }
        }

        return card;
    }

    @Override
    public void issueCard(Card card) {
        card.setIssueDate(LocalDate.now());
        card.setStatus("ACTIVE");
        cardRepository.save(card);

        try {
            User user = userService.getUserById(card.getUserId());
            auditLogService.logAction(
                    "CARD_ISSUED",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "Card issued to user ID " + user.getUserId() +
                            " with expiry " + card.getExpiryDate(),
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (CARD_ISSUED): " + e.getMessage());
        }
    }

    @Override
    public List<Card> getCardsByUserId(Long userId) {
        List<Card> cards = cardRepository.findByUserId(userId);

        try {
            auditLogService.logAction(
                    "CARDS_FETCHED_BY_USER",
                    "system",
                    userId,
                    "user-" + userId,
                    "Fetched " + cards.size() + " cards for user ID: " + userId,
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (CARDS_FETCHED_BY_USER): " + e.getMessage());
        }

        return cards;
    }

    private String generateCardNumber() {
        return String.format("%04d%04d%04d%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }

    private String generateCVV() {
        return String.format("%03d", random.nextInt(1000));
    }
}
