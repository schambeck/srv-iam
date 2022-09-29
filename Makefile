APP = srv-iam
VERSION = 1.0.0
JAR = ${APP}-${VERSION}.jar
TARGET_JAR = build/libs/${JAR}
#JAVA_OPTS = -Dserver.port=0
DOCKER_IMAGE = ${APP}:latest

# Gradle

clean:
	./gradlew clean

all: clean
	./gradlew build

install: clean
	./gradlew jar

check: clean
	./gradlew check

check-unit: clean
	./gradlew test

check-integration: clean
	./gradlew check

dist: clean
	./gradlew bootJar

dist-run: dist run

run:
	java ${JAVA_OPTS} -jar ${TARGET_JAR}

# Docker

dist-docker-build-deploy: dist docker-build stack-app-deploy

dist-docker-build: dist docker-build

dist-docker-build-push: dist docker-build docker-push

docker-build-push: docker-build docker-push

docker-build:
	DOCKER_BUILDKIT=1 docker build -f ${DOCKER_CONF} -t ${DOCKER_IMAGE} --build-arg=JAR_FILE=${JAR} target

docker-run:
	docker run -d \
		--restart=always \
		--net schambeck-bridge \
		--name ${APP} \
	  	--env SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/iam \
		--env SPRING_DATASOURCE_USERNAME=postgres \
		--env SPRING_DATASOURCE_PASSWORD=postgres \
		--publish 8080:8080 \
		${DOCKER_IMAGE}

docker-run-db:
	docker run -d \
		--restart=always \
		--net schambeck-bridge \
		--name db \
	  	--env MYSQL_ROOT_PASSWORD=example \
		--publish 3306:3306 \
		mysql

--rm-docker-image:
	docker rmi ${DOCKER_IMAGE}

docker-bash:
	docker exec -it docker_web_1 /bin/bash

docker-tag:
	docker tag ${APP} ${DOCKER_IMAGE}

docker-push:
	docker push ${DOCKER_IMAGE}

docker-pull:
	docker pull ${DOCKER_IMAGE}

# Compose

dist-compose-up: dist compose-up

dist-docker-build-compose-up: dist docker-build compose-up

compose-up:
	docker-compose -p ${APP} -f ${COMPOSE_CONF} up -d --build

compose-down: --compose-down

compose-down-rmi: --compose-down --rm-docker-image

--compose-down:
	docker-compose -p ${APP} -f ${COMPOSE_CONF} down

compose-logs:
	docker-compose -f ${COMPOSE_CONF} logs -f \web

# Swarm

stack-deploy:
	docker stack deploy -c ${STACK_CONF} --with-registry-auth ${APP}

service-logs:
	docker service logs ${APP}_web -f
