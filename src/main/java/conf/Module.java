/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mashape.unirest.http.Unirest;
import dao.MealDao;
import dao.MealDaoImpl;
import dao.TagDao;
import dao.TagDaoImpl;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.MealService;
import services.MealServiceImpl;
import services.MessageService;
import services.MessageServiceMQTT;
import utils.UnirestObjectMapper;

import java.util.UUID;

@Singleton
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        Unirest.setObjectMapper(new UnirestObjectMapper());

        bind(MealDao.class).to(MealDaoImpl.class);
        bind(TagDao.class).to(TagDaoImpl.class);
        bind(MessageService.class).to(MessageServiceMQTT.class);
        bind(MealService.class).to(MealServiceImpl.class);
    }

    @Provides
    public IMqttClient provideMqttClient() {
        IMqttClient mqttClient = null;
        final String brokerUrl = "tcp://138.68.197.76:1883";
        final String clientId = UUID.randomUUID().toString();
        final MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, persistence);
        } catch (MqttException me) {
            me.printStackTrace();
        }

        return mqttClient;
    }

}
