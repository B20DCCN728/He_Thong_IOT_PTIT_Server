package com.example.he_thong_iot_ptit.configuration.mqttconfiguration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.Objects;

// Configure MQTT connection to broker (localhost:1883)
@Configuration
public class MQTTConfiguration {

    //    @Bean
    //    public MqttPahoClientFactory mqttClientFactory() {
    //        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    //        MqttConnectOptions options = new MqttConnectOptions();
    //        options.setServerURIs(new String[] { "tcp://host1:1883", "tcp://host2:1883" });
    //        options.setUserName("username");
    //        options.setPassword("password".toCharArray());
    //        factory.setConnectionOptions(options);
    //        return factory;
    //    }
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{"tcp://localhost:1883"});

        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;
    }

    // Configure inbound channel adapter to receive messages from broker
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "serverIn",
                        mqttClientFactory(),
                        "tro/esp/dht/temperature",
                        "tro/esp/dht/humidity",
                        "tro/esp/lm393/lightvalue",
                        "tro/esp/lm393/voltage"

        );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
//                if(topic.equals("tro/esp/dht/temperature")) {
//                    System.out.println("topic 1 is " + topic);
//                }

                System.out.println(message.getPayload());
            }
        };
    }

    //Configure outbound channel adapter to send messages to broker

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        "serverOut",
                        mqttClientFactory()
                );
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("#");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {

        void sendToMqtt(String data);

    }
}
