package com.emansy.employeeservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic attendingEmployeesRequestTopic() {
        return TopicBuilder.name("attending_employees_request").build();
    }

    @Bean
    public NewTopic attendingEmployeesResponseTopic() {
        return TopicBuilder.name("attending_employees_response").build();
    }

    @Bean
    public NewTopic nonAttendingEmployeesRequestTopic() {
        return TopicBuilder.name("non_attending_employees_request").build();
    }

    @Bean
    public NewTopic nonAttendingEmployeesResponseTopic() {
        return TopicBuilder.name("non_attending_employees_response").build();
    }

    @Bean
    public NewTopic unattendRequestTopic() {
        return TopicBuilder.name("unattend_request").build();
    }

    @Bean
    public NewTopic unattendNotificationRequestTopic() {
        return TopicBuilder.name("unattend_notification_request").build();
    }
}
