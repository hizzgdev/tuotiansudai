package com.tuotiansudai.mq.consumer.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tuotiansudai.fudian.message.BankBindCardMessage;
import com.tuotiansudai.mq.client.model.MessageQueue;
import com.tuotiansudai.mq.consumer.MessageConsumer;
import com.tuotiansudai.service.BankBindCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class BankUnbindCardMessageConsumer implements MessageConsumer {

    private static Logger logger = LoggerFactory.getLogger(BankUnbindCardMessageConsumer.class);

    private final BankBindCardService bankBindCardService;

    @Autowired
    public BankUnbindCardMessageConsumer(BankBindCardService bankBindCardService) {
        this.bankBindCardService = bankBindCardService;
    }

    @Override
    public MessageQueue queue() {
        return MessageQueue.UnbindBankCard_Success;
    }

    @Override
    public void consume(String message) {
        logger.info("[MQ] receive message: {}: {}.", this.queue(), message);

        try {
            BankBindCardMessage bankBindCardMessage = new Gson().fromJson(message, BankBindCardMessage.class);
            bankBindCardService.processUnbind(bankBindCardMessage);
        } catch (JsonSyntaxException e) {
            logger.error(MessageFormat.format("[MQ] consume message error, message: {0}", message), e);
        }
    }
}