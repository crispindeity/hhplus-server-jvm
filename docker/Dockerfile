FROM pinpointdocker/pinpoint-agent:2.5.3 AS pinpoint
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=pinpoint /pinpoint-agent /pinpoint-agent

COPY build/libs/*.jar app.jar

ENV PINPOINT_AGENT_ID=backend-server
ENV PINPOINT_APPLICATION_NAME=hhplus-backend
ENV PINPOINT_COLLECTOR_IP=pinpoint-quickstart
ENV PINPOINT_GRPC_AGENT_PORT=9991
ENV PINPOINT_GRPC_STAT_PORT=9992
ENV PINPOINT_GRPC_SPAN_PORT=9993

ENTRYPOINT ["sh", "-c", "\
  java \
  -javaagent:/pinpoint-agent/pinpoint-bootstrap.jar \
  -Dpinpoint.agentId=${PINPOINT_AGENT_ID} \
  -Dpinpoint.applicationName=${PINPOINT_APPLICATION_NAME} \
  -Dpinpoint.config=/pinpoint-agent/pinpoint-root.config \
  -Dprofiler.transport.grpc.collector.ip=${PINPOINT_COLLECTOR_IP} \
  -Dprofiler.transport.grpc.agent.port=${PINPOINT_GRPC_AGENT_PORT} \
  -Dprofiler.transport.grpc.stat.port=${PINPOINT_GRPC_STAT_PORT} \
  -Dprofiler.transport.grpc.span.port=${PINPOINT_GRPC_SPAN_PORT} \
  -jar app.jar"]
