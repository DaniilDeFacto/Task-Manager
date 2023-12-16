FROM gradle:8.4-jdk20

WORKDIR /app

COPY . .

RUN gradle installDist

CMD ./build/install/app/bin/app --spring.profiles.active=prod