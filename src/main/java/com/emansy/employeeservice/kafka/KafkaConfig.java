package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventIdDto;
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
    public ReplyingKafkaTemplate<String, EventIdDto, Set<EmployeeDto>> employeesReplyingKafkaTemplate(
            ProducerFactory<String, EventIdDto> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, Set<EmployeeDto>> listenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, Set<EmployeeDto>>
                replyContainer = listenerContainerFactory.createContainer("employees-response");
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId("employee-group");
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }
    
    @Bean
    public KafkaTemplate<String, Set<EmployeeDto>> employeesReplyTemplate(
            ProducerFactory<String, Set<EmployeeDto>> producerFactory,
            ConcurrentKafkaListenerContainerFactory<String, Set<EmployeeDto>> listenerContainerFactory) {
        KafkaTemplate<String, Set<EmployeeDto>> kafkaTemplate = new KafkaTemplate<>(producerFactory);
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
    public NewTopic attendanceNotificationTopic() { return TopicBuilder.name("attendance-notification").build();
    }
}
