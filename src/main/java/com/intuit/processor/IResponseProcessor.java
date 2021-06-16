package com.intuit.processor;

import com.intuit.common.model.mq.EventReply;

public interface IResponseProcessor {
    void process(EventReply eventReply);
}
