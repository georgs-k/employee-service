package com.emansy.employeeservice.config;

import com.emansy.employeeservice.model.AttendeeIdsDto;
import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.EventIdsWithinDatesDto;
import com.emansy.employeeservice.model.EventsDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

    // temporary - emulation of event-service:
    @Bean
    public ReplyingKafkaTemplate<String, AttendeeIdsDto, EventDto> attendanceReplyingKafkaTemplate(
            ProducerFactory<String, AttendeeIdsDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, EventDto> listenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, EventDto>
                replyContainer = listenerContainerFactory.createContainer("attendance-response");
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId("employee-group");
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, EventDto> attendanceReplyTemplate(
            ProducerFactory<String, EventDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, EventDto> listenerContainerFactory) {
        KafkaTemplate<String, EventDto> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        listenerContainerFactory.getContainerProperties().setMissingTopicsFatal(false);
        listenerContainerFactory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }
    // temporary end

    @Bean
    public ReplyingKafkaTemplate<String, EventIdsWithinDatesDto, EventsDto> eventsReplyingKafkaTemplate(
            ProducerFactory<String, EventIdsWithinDatesDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, EventsDto> listenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, EventsDto>
                replyContainer = listenerContainerFactory.createContainer("events-response");
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId("event-group");
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, EventsDto> eventsReplyTemplate(
            ProducerFactory<String, EventsDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, EventsDto> listenerContainerFactory) {
        KafkaTemplate<String, EventsDto> kafkaTemplate = new KafkaTemplate<>(producerFactory);
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
