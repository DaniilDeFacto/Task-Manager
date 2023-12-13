FROM gradle:8.4-jdk20

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/app/bin/app