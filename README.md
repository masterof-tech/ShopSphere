# ShopSphere

ShopSphere is an e-commerce web application built for the RTU Advanced Java syllabus using Servlets, JSP, JDBC and MySQL.

## Features

- User registration, login and logout
- Product listing with search, filter, sort and product details
- Shopping cart and checkout with saved addresses and coupons
- Order history, order details and order cancellation
- Wishlist and product reviews
- Profile management and password change
- Admin panel: products, categories, orders, customers, coupons and sales summary

## Tech Stack

- Java 17
- Servlets, JSP and JSTL
- MySQL 8 with JDBC
- Apache Tomcat 11
- Maven

## Project Structure

```
ShopSphere
|-- database
|   `-- shopsphere.sql
`-- web
    |-- pom.xml
    `-- src/main
        |-- java/com/shopsphere   (controller, dao, filter, listener, model, service, util)
        |-- resources             (db.properties.example)
        `-- webapp                (JSP pages, css, js, WEB-INF)
```

## Setup

1. Install JDK 17, Maven, MySQL 8 and Tomcat 11.
2. Create the database by running `database/shopsphere.sql` in MySQL Workbench.
3. Go to `web/src/main/resources`, copy `db.properties.example` and rename the copy to `db.properties`.
4. Open `db.properties` and set your own MySQL password in `db.password`.
5. Open a terminal in the `web` folder and run:
```
   mvn clean package
```
6. Copy the generated `.war` file from `web/target` into the `webapps` folder of Tomcat and start Tomcat.
7. Open `http://localhost:8080/shopsphere/` in your browser.

## Note

`db.properties` contains local credentials and is excluded from Git through `.gitignore`.