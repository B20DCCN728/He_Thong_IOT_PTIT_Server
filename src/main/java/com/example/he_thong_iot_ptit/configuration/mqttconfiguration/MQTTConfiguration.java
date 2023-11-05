package com.example.he_thong_iot_ptit.configuration.mqttconfiguration;

import com.example.he_thong_iot_ptit.model.Sensor;
import com.example.he_thong_iot_ptit.repository.DeviceRespository;
import com.example.he_thong_iot_ptit.repository.SensorRepository;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessageSendingOperations;


// Configure MQTT connection to broker (localhost:1883)
@Configuration
public class MQTTConfiguration {
    // Define current time
    private LocalDateTime currentTime;

    // Define date time formatter
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Define Simp Message SendingOperations
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    DeviceRespository deviceRespository;

    // Configure MQTT connection to broker (localhost:1883)
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        //Define connection options
        options.setServerURIs(new String[]{"tcp://localhost:1883"});

        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;
    }

    // Configure inbound channel adapter to receive messages from broker
    @Bean
    public MessageChannel sensorMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "serverIn",
                        mqttClientFactory(),
                        "tro/esp/sensor_data"
        );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(sensorMqttInputChannel());
        return adapter;
    }

    // Configure message handler to process messages from sensorMqttInputChannel
    @Bean
    @ServiceActivator(inputChannel = "sensorMqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException, NullPointerException {
                String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
                if(topic.equals("tro/esp/sensor_data")) {
                    // Get current time
                    currentTime = LocalDateTime.now();

                    // Get payload from message and split it into an array of strings
                    String [] payload = message
                            .getPayload()
                            .toString()
                            .trim()
                            .split("\\s+");

                    // Create a new sensor object
                    Sensor sensor = new Sensor(
                            currentTime,
                            Double.parseDouble(payload[0]),
                            Double.parseDouble(payload[1]),
                            Double.parseDouble(payload[2]),
                            Double.parseDouble(payload[3])
                    );

                    // Save sensor object to database
                    sensorRepository.save(sensor);

                    System.out.println(sensor);

                    simpMessageSendingOperations.convertAndSend("/sensor", sensor);

                    // Setting for Websocket to send data to client

                }
                else System.out.println("Topic: " + topic + " Payload: " + message.getPayload());
            }
        };
    }

    // LED
    //Configure outbound channel adapter to send led messages to broker
    @Bean
    @ServiceActivator(inputChannel = "ledMqttOutboundChannel")
    public MessageHandler ledMqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        "serverOut",
                        mqttClientFactory()
                );
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("tro/esp/led");
        return messageHandler;
    }

    // Configure led outbound channel
    @Bean
    public MessageChannel ledMqttOutboundChannel() {
        return new DirectChannel();
    }

    // Define gateway interface for led
    @MessagingGateway(defaultRequestChannel = "ledMqttOutboundChannel")
    public interface MyLedGateway {

        void sendToMqtt(String data);

    }

    // FAN
    //Configure outbound channel adapter to send fan messages to broker
    @Bean
    @ServiceActivator(inputChannel = "fanMqttOutboundChannel")
    public MessageHandler fanMqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                "serverIn",
                mqttClientFactory()
        );
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("tro/esp/fan");

        return messageHandler;
    }

    // Define fan Outbound channel
    @Bean
    public MessageChannel fanMqttOutboundChannel() {
        return new DirectChannel();
    }

    // Define gateway interface for fan
    @MessagingGateway(defaultRequestChannel = "fanMqttOutboundChannel")
    public interface MyFanGateway {
        void sendToMqtt(String data);
    }
}
