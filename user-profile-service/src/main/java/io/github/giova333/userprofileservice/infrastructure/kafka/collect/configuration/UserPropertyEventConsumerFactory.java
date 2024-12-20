package io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration;

import io.github.giova333.userprofileservice.infrastructure.kafka.collect.UserPropertyEventConsumer;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserPropertyEventConsumerFactory implements BeanDefinitionRegistryPostProcessor {

    ApplicationContext applicationContext;
    KafkaTopicsConfiguration kafkaTopicsConfiguration;
    Environment environment;

    public UserPropertyEventConsumerFactory(ApplicationContext applicationContext,
                                            Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.kafkaTopicsConfiguration =
                Binder.get(environment)
                        .bind("", Bindable.of(KafkaTopicsConfiguration.class))
                        .orElseThrow(IllegalStateException::new);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (var topic : kafkaTopicsConfiguration.getKafkaTopics()) {
            registry.registerBeanDefinition(
                    generateContainerPropertiesBeanName(topic.getTopic()),
                    containerPropertiesBean(topic));

            registry.registerBeanDefinition(
                    generateConsumerFactoryBeanName(topic.getTopic()),
                    kafkaConsumerFactoryBean(topic)
            );

            registry.registerBeanDefinition(
                    generateConcurrentMessageListenerContainerBeanName(topic.getTopic()),
                    concurrentMessageListenerContainerBean(topic));
        }
    }

    public GenericBeanDefinition concurrentMessageListenerContainerBean(
            KafkaTopicsConfiguration.KafkaTopicConfiguration configuration) {

        var beanDefinition = new GenericBeanDefinition();
        var beanName = generateConcurrentMessageListenerContainerBeanName(configuration.getTopic());

        beanDefinition.setInstanceSupplier(() -> {

            var consumerFactory = applicationContext.getBean(generateConsumerFactoryBeanName(configuration.getTopic()));
            var containerProperties = applicationContext.getBean(generateContainerPropertiesBeanName(configuration.getTopic()));

            var container = new ConcurrentMessageListenerContainer<>(
                    ((ConsumerFactory) consumerFactory),
                    (ContainerProperties) containerProperties);

            container.setBeanName(beanName);

            var consumer = applicationContext.getBean(UserPropertyEventConsumer.class);

            container.setupMessageListener(consumer);
            container.setConcurrency(configuration.getConcurrency());
            container.setCommonErrorHandler(new DefaultErrorHandler());

            return container;
        });
        return beanDefinition;
    }

    private GenericBeanDefinition containerPropertiesBean(
            KafkaTopicsConfiguration.KafkaTopicConfiguration kafkaTopicConfiguration) {

        var containerProperties = new ContainerProperties(kafkaTopicConfiguration.getTopic());
        var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setInstanceSupplier(() -> containerProperties);
        return beanDefinition;
    }

    private GenericBeanDefinition kafkaConsumerFactoryBean(
            KafkaTopicsConfiguration.KafkaTopicConfiguration kafkaTopicConfiguration) {

        var beanDefinition = new GenericBeanDefinition();

        beanDefinition.setInstanceSupplier(() -> {
            var kafkaProperties = applicationContext.getBean(KafkaProperties.class);

            var props = kafkaProperties.buildConsumerProperties(null);

            kafkaTopicConfiguration.getProperties()
                    .forEach((key, value) -> props.put((String) key, value.toString()));

            var consumerGroup = environment.getProperty("spring.application.name");

            props.put(
                    ConsumerConfig.GROUP_ID_CONFIG,
                    consumerGroup);

            Deserializer<byte[]> keyDeserializer = new ByteArrayDeserializer();
            Deserializer<Bytes> valueDeserializer = new ErrorHandlingDeserializer<>(
                    new BytesDeserializer());
            return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
        });

        return beanDefinition;
    }

    private static String generateConsumerFactoryBeanName(String topic) {
        return ConsumerFactory.class.getSimpleName() + "-" + topic;
    }

    private static String generateContainerPropertiesBeanName(String topic) {
        return ContainerProperties.class.getSimpleName() + "-" + topic;
    }

    private static String generateConcurrentMessageListenerContainerBeanName(String topic) {
        return ConcurrentMessageListenerContainer.class.getSimpleName() + "-" + topic;
    }

}
