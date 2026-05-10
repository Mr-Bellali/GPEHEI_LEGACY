FROM php:8.4.14-apache

RUN docker-php-ext-install pdo pdo_mysql

# Enabled mod_rewrite and allow .htaccess
RUN a2enmod rewrite && \
    sed -i 's/AllowOverride None/AllowOverride All/g' /etc/apache2/apache2.conf

RUN mv "$PHP_INI_DIR/php.ini-production" "$PHP_INI_DIR/php.ini"

COPY ./src /var/www/html

USER www-data