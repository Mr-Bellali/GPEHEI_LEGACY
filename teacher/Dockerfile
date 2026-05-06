FROM php:8.4.14-apache

RUN docker-php-ext-install pdo pdo_mysql

RUN mv "$PHP_INI_DIR/php.ini-production" "$PHP_INI_DIR/php.ini"

# Copy application files to document root
COPY ./src /var/www/html

USER www-data