# Spring Boot + Thymeleaf Demo

This project demonstrates:
- **Session management** with login/logout  
- **Form handling & validation** (e.g., book purchase, login form)  
- **Static vs. template resources** (e.g., `/privacy` as static, others as Thymeleaf templates)  

Currently, the `username` for the logged-in user is added in **each controller** manually.  
In a future improvement, this can be simplified by using a **Spring Interceptor** to set it globally for all pages.

---
For now you have to run by the "Run and Debug" view and tapping the "play" butto there
mvn spring-boot:run

---
We're using a local sqlite.db but if needed we can add API to connect to a remotely hosted DB
