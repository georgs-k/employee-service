package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdsWithinDatesDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.Set;

@Configuration
public class KafkaConfig {

    @Bean
    public ReplyingKafkaTemplate<String, EventIdsWithinDatesDto, Set<EventDto>> eventsReplyingKafkaTemplate(
            ProducerFactory<String, EventIdsWithinDatesDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, Set<EventDto>> listenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, Set<EventDto>>
                replyContainer = listenerContainerFactory.createContainer("events-response");
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId("event-group");
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, Set<EventDto>> eventsReplyTemplate(
            ProducerFactory<String, Set<EventDto>> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, Set<EventDto>> listenerContainerFactory) {
        KafkaTemplate<String, Set<EventDto>> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        listenerContainerFactory.getContainerProperties().setMissingTopicsFatal(false);
        listenerContainerFactory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, AttendeesDto> attendanceKafkaTemplate(ProducerFactory<String, AttendeesDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic employeesRequestTopic() { return TopicBuilder.name("employees-request").build();
    }

    @Bean
    public NewTopic employeesResponseTopic() { return TopicBuilder.name("employees-response").build();
    }

    @Bean
    public NewTopic eventsRequestTopic() { return TopicBuilder.name("events-request").build();
    }

    @Bean
    public NewTopic eventsResponseTopic() { return TopicBuilder.name("events-response").build();
    }

    @Bean
    public NewTopic attendanceRequestTopic() { return TopicBuilder.name("attendance-request").build();
    }

    @Bean
    public NewTopic attendanceResponseTopic() { return TopicBuilder.name("attendance-response").build();
    }

    @Bean
    public NewTopic attendanceNotificationTopic() { return TopicBuilder.name("attendance-notification").build();
    }
}
