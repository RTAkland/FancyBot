FROM nginx:alpine as nginx-server

RUN echo "server { listen 8000; location / { return 200 'OK'; add_header Content-Type text/plain; } }" \
    > /etc/nginx/conf.d/default.conf

FROM openjdk:17-alpine

WORKDIR /app

COPY build/libs/FancyBot-1.0-SNAPSHOT-all.jar /app/app.jar

COPY --from=nginx-server /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf

CMD ["sh", "-c", "nginx && java -jar app.jar"]

EXPOSE 8000
