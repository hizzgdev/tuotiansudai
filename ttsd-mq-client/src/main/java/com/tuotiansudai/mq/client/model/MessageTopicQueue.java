package com.tuotiansudai.mq.client.model;

public enum MessageTopicQueue {
    UserRegistered_CouponGiving(MessageTopic.UserRegistered, "UserRegistered-CouponGiving");

    final MessageTopic topic;
    final String queueName;

    MessageTopicQueue(MessageTopic topic, String queueName) {
        this.topic = topic;
        this.queueName = queueName;
    }

    public MessageTopic getTopic() {
        return topic;
    }

    public String getQueueName() {
        return queueName;
    }
}
