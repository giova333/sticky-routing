package io.github.giova333.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;
import static java.lang.Thread.startVirtualThread;

public class KafkaDataSender {

    public static final String HOST = System.getenv().getOrDefault("KAFKA_HOST", "localhost:9092");
    public static final long DELAY_BETWEEN_SEND = Long.parseLong(System.getenv().getOrDefault("DELAY_BETWEEN_SEND", "100"));
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {

        var countDownLatch = new CountDownLatch(1);

        startVirtualThread(KafkaDataSender::produceUserRegisteredEvents);
        startVirtualThread(KafkaDataSender::produceEventPurchases);

        countDownLatch.await();
    }

    record UserRegisteredEvent(String userId, String email, int age, String userCountry) {
    }

    record EventPurchase(String uid, int purchaseCount) {

    }

    @SneakyThrows
    private static void produceUserRegisteredEvents() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

        Faker faker = new Faker();

        while (true) {
            for (int i = 1; i <= 1000; i++) {
                var event = new UserRegisteredEvent(String.valueOf(i),
                        faker.internet().emailAddress(),
                        faker.number().numberBetween(18, 60),
                        faker.address().countryCode());

                var eventJson = MAPPER.writeValueAsString(event);
                var record = new ProducerRecord<>("user-registered", event.userId(), eventJson);
                producer.send(record).get();
                System.out.println("Produced: " + eventJson);
                sleep(DELAY_BETWEEN_SEND);
            }
        }
    }

    @SneakyThrows
    private static void produceEventPurchases() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

        var userRegisteredEvents = List.of(
                new EventPurchase("1", 123),
                new EventPurchase("2", 15),
                new EventPurchase("3", 92),
                new EventPurchase("4", 3218),
                new EventPurchase("5", 213)
        );

        Faker faker = new Faker();

        while (true) {
            for (int i = 1; i <= 1000; i++) {
                var event = new EventPurchase(String.valueOf(i), faker.number().numberBetween(1, 1000));
                var eventJson = MAPPER.writeValueAsString(event);
                var record = new ProducerRecord<>("event-purchase", event.uid(), eventJson);
                producer.send(record).get();
                System.out.println("Produced: " + eventJson);
                sleep(DELAY_BETWEEN_SEND);
            }
        }
    }

    private static void createTopic(String topic, int partitions) {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, HOST
        );
        Admin admin = Admin.create(properties);
        NewTopic newTopic = new NewTopic(topic, partitions, (short) 1);

        try {
            admin.createTopics(List.of(newTopic)).values().get(topic).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error creating topic: " + topic);
        }
    }
}
