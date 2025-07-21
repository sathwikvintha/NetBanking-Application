package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Card;
import java.util.List;

public interface CardService {
    Card addCard(Card card);
    List<Card> getCardsByUserId(Long userId);
    List<Card> getAllCards();
    Card getCardById(Long cardId);
    void issueCard(Card card);
    List<Card> getCardsByStatus(String status);
    void updateCardStatus(Long cardId, String newStatus);

}
