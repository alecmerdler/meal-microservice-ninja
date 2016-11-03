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


import controllers.ApplicationController;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    private String apiUrl = "/api/v1";
    private String mealsUrl = apiUrl + "/meals";
    private String tagsUrl = apiUrl + "/tags";

    @Override
    public void init(Router router) {
        router.GET().route("/").with(Results.json().render("name", "meal-microservice-ninja"));
        router.GET().route("/healthcheck").with(Results.json().render("status", "running"));
        router.POST().route("/initialize").with(ApplicationController.class, "initialize");

        router.GET().route("/messages").with(ApplicationController.class, "listMessages");

        router.GET().route(mealsUrl).with(ApplicationController.class, "listMeals");
        router.POST().route(mealsUrl).with(ApplicationController.class, "createMeal");
        router.GET().route(mealsUrl + "/{id}").with(ApplicationController.class, "retrieveMeal");
        router.PUT().route(mealsUrl + "/{id}").with(ApplicationController.class, "updateMeal");
        router.DELETE().route(mealsUrl + "/{id}").with(ApplicationController.class, "destroyMeal");
        router.POST().route(mealsUrl + "/{id}/purchase").with(ApplicationController.class, "purchaseMeal");
    }
}
