package kr.hhplus.be.server.config.kafka

import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import java.io.Serializable
import kr.hhplus.be.server.reservation.application.event.ReservationEvent
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
internal class KafkaConfig(
    private val kafkaProperties: KafkaProperties
) {
    @Bean
    fun reservationEventConsumerFactory(): ConsumerFactory<String, ReservationEvent> {
        val configs: Map<String, Serializable> =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to
                    ErrorHandlingDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to
                    ErrorHandlingDeserializer::class.java,
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS to StringDeserializer::class.java,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java,
                JsonDeserializer.VALUE_DEFAULT_TYPE to ReservationEvent::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*",
                JsonDeserializer.USE_TYPE_INFO_HEADERS to false
            )

        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun reservationEventKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ReservationEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ReservationEvent>()
        factory.consumerFactory = reservationEventConsumerFactory()
        return factory
    }

    @Bean
    fun genericConsumerFactory(): ConsumerFactory<String, Any> {
        val configs: Map<String, Serializable> =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to
                    ErrorHandlingDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to
                    ErrorHandlingDeserializer::class.java,
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS to StringDeserializer::class.java,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*",
                JsonDeserializer.USE_TYPE_INFO_HEADERS to true
            )
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun genericKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = genericConsumerFactory()
        return factory
    }

    @Bean
    fun cdcConsumerFactory(): ConsumerFactory<String, String> {
        val configs: Map<String, Serializable> =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ConsumerConfig.GROUP_ID_CONFIG to "real-order-cdc-processor",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true,
                ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to 1000
            )
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun cdcKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = cdcConsumerFactory()
        return factory
    }

    @Bean
    fun avroConsumerFactory(): ConsumerFactory<String, GenericRecord> {
        val configs =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java,
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to
                    kafkaProperties.schemaRegistryUrl,
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to false,
                AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS to true
            )

        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun avroKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, GenericRecord> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, GenericRecord>()
        factory.consumerFactory = avroConsumerFactory()
        return factory
    }

    @Bean
    fun orderEventProducerFactory(): ProducerFactory<String, ReservationEvent> {
        val configs: Map<String, Serializable> =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
                JsonSerializer.ADD_TYPE_INFO_HEADERS to true
            )
        return DefaultKafkaProducerFactory(configs)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, ReservationEvent> =
        KafkaTemplate(orderEventProducerFactory())

    @Bean
    fun avroProducerFactory(): ProducerFactory<String, GenericRecord> {
        val configs: Map<String, Serializable> =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java,
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to
                    kafkaProperties.schemaRegistryUrl,
                AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS to true,
                ProducerConfig.ACKS_CONFIG to "1",
                ProducerConfig.RETRIES_CONFIG to 3,
                ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                ProducerConfig.LINGER_MS_CONFIG to 10,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to "snappy"
            )
        return DefaultKafkaProducerFactory(configs)
    }

    @Bean
    fun avroKafkaTemplate(): KafkaTemplate<String, GenericRecord> =
        KafkaTemplate(avroProducerFactory())
}
