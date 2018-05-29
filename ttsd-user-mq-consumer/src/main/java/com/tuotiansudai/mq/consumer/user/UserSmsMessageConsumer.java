package com.tuotiansudai.mq.consumer.user;

import com.google.common.base.Strings;
import com.tuotiansudai.client.SmsWrapperClient;
import com.tuotiansudai.dto.sms.SmsDto;
import com.tuotiansudai.mq.client.model.MessageQueue;
import com.tuotiansudai.mq.consumer.MessageConsumer;
import com.tuotiansudai.util.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSmsMessageConsumer implements MessageConsumer{
    private static Logger logger = LoggerFactory.getLogger(UserSmsMessageConsumer.class);

    @Autowired
    private SmsWrapperClient smsWrapperClient;

    @Override
    public MessageQueue queue() {
        return MessageQueue.UserSms;
    }

    @Override
    public void consume(String message) {
        logger.info("[MQ] receive message: {}: {}.", this.queue(), message);
        if (Strings.isNullOrEmpty(message)){
            logger.error("[MQ] parse message failed: {}: '{}'.", this.queue(), message);
        }

        try {
            SmsDto smsDto = JsonConverter.readValue(message, SmsDto.class);
            smsWrapperClient.sendSms(smsDto);

        }catch (Exception e){
            logger.error("[MQ] 程序內部异常: {}: {}.{}", this.queue(), message, e.getMessage());
        }

    }
}
