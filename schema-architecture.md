# Architecture
This Spring Boot application follows a layered architecture with separate MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs handle the remaining application features.
The application uses MySQL for relational data and MongoDB for prescription documents. All requests pass through a common service layer, which contains the business logic and delegates data operations to the appropriate repositories.

# Data Flow
The user accesses either a dashboard page (Admin or Doctor) or a REST module such as Appointments or Patient Records.
The request is routed to the appropriate Thymeleaf Controller or REST Controller.
The controller forwards the request to the common Service Layer.
The Service Layer processes the business logic and determines which data source is needed.
For relational data, the Service Layer communicates with the MySQL Repository, which accesses the MySQL database through JPA entities.
For prescription data, the Service Layer communicates with the MongoDB Repository, which accesses MongoDB using document models.
The retrieved data is returned through the Service Layer to the controller, and the final response is sent back to the user as either a rendered Thymeleaf page or a REST API response.
