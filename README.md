# Event Management System - Employee microservice
This is a training project that consists of three microservices: employee-service, event-service, and notification-service. The microservices interact with each other using Apache Kafka. Employee-service and event-service are REST APIs, whose endpoints are open to two corresponding clients. Employee-service also utitilizes a public REST API as an external dependency.

![Event Management System](https://user-images.githubusercontent.com/108323637/232474276-4526d704-dc59-4c3c-b4df-1cdbfbd812a6.png)

Event Management System is designed for managing employee data for an international company, and for creating and managing online events for the employees, based on their schedules, working times and public holidays in corresponding countries. The system subscribes employees to events, or reschedules the events to nearest available timeslots that suit all invited employees.
Here is a draft outline of service methods and their interactions:

![Event Management System, service methods](https://user-images.githubusercontent.com/108323637/232480104-ecffcaac-eeee-4ab4-b755-8894b5d7fe1b.png)
