package kr.hhplus.be.server.config.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "kafka")
data class KafkaProperties(
    val bootstrapServer: String,
    val schemaRegistryUrl: String,
    @NestedConfigurationProperty
    val topics: Map<TopicKey, Topic>
) {
    enum class TopicKey {
        RESERVATION
    }

    data class Topic(
        val name: String
    )

    val reservationTopic: String
        get() = topics.getValue(TopicKey.RESERVATION).name
}
