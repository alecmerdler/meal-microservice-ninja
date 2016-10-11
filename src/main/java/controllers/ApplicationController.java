/**
 * Copyright (C) 2013 the original author or authors.
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

package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import ninja.Result;
import ninja.Results;
import rx.Observable;
import rx.Subscriber;
import utils.UnirestObjectMapper;

import java.util.HashMap;
import java.util.Map;


@Singleton
public class ApplicationController {

    private String serviceUrl = "http://localhost:8080";
    private ObjectMapper objectMapper;

    @Inject
    public ApplicationController() {
        Unirest.setObjectMapper(new UnirestObjectMapper());
        this.objectMapper = new ObjectMapper();
    }

    public Result subscribeTest() {
        Result response = Results.json();

        makeRequest()
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Map<String, Object> responseBody) {
                        response.json().render(responseBody);
                    }
                });

        return response;
    }

    private Observable<Map<String, Object>> makeRequest() {
        return Observable.defer(() -> {
            Map responseBody = new HashMap();
            try {
                HttpResponse<JsonNode> response = Unirest.get(serviceUrl)
                        .asJson();
                responseBody = objectMapper.readValue(response.getBody().getObject().toString(), HashMap.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return Observable.just(responseBody);
        });
    }
}
