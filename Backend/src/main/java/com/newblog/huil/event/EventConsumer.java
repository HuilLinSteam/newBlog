package com.newblog.huil.event;

import com.alibaba.fastjson.JSONObject;
import com.newblog.huil.entity.Blog;
import com.newblog.huil.entity.Event;
import com.newblog.huil.entity.Message;
import com.newblog.huil.service.BlogService;
import com.newblog.huil.service.ElasticSearchService;
import com.newblog.huil.service.MessageService;
import com.newblog.huil.utils.NewBlogConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuilLIN
 */
@Component
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @KafkaListener(topics = {NewBlogConstant.TOPIC_COMMENT, NewBlogConstant.TOPIC_LIKE, NewBlogConstant.TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容不能为空!");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        // 发送站内通知
        Message message = new Message();
        message.setFromId(NewBlogConstant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityUserId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    /**
     * 发布博客事件消费
     *
     * @param record
     */
    @KafkaListener(topics = {NewBlogConstant.TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        Blog blog = blogService.findBlogById(event.getEntityId());

        elasticSearchService.saveBlog(blog);
    }

    /**
     * 删除博客
     * @param record
     */
    @KafkaListener(topics = {NewBlogConstant.TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
        // 这里的删除只是删除ES的内容！并不会删除到数据库
        elasticSearchService.deleteBlog(event.getEntityId());
    }

}
