E-Wallet App Backend
Project Overview
This project is the backend for an E-Wallet application, similar to Paytm, designed with a microservices architecture using Spring Boot. It aims to offer a scalable, secure, and robust platform for managing financial transactions, including wallet operations and third-party payments. Key technologies include Apache Kafka for event streaming, Redis for caching, Java Mail Sender for notifications, Spring Security for authentication, multithreading for performance optimization, MySQL for persistent storage, and RazorPay Payment Gateway integration for adding funds to the wallet.

Features
Microservices Architecture: Modular design for easy maintenance, scalability, and deployment.
User Registration and Authentication: Secure signup and login with JWT, managed via Spring Security.
Wallet Transactions: Support for adding money, transferring funds, and withdrawing, with RazorPay integration for seamless payments.
Apache Kafka Integration: Utilizes Kafka for reliable event streaming and processing across services.
Redis Caching: Enhances performance by caching frequent queries and results.
Java Mail Sender: For sending emails related to transactions, account alerts, and promotions.
Multithreading: Leverages Java's concurrency features for handling multiple tasks simultaneously, improving performance.
Data Persistence: Uses MySQL for storing user data, transaction records, and other persistent information.
