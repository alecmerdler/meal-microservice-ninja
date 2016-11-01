package models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alec on 10/5/16.
 */
@MappedSuperclass
public abstract class Model {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    protected Long id;

    public Model() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> mapProperties() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", this.id);
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0 &&
                method.getReturnType() != void.class &&
                method.getName().matches("^(get).+")) {
                String name = method.getName().replaceAll("^(get)", "");
                name = Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
                Object value = method.invoke(this);
                properties.put(name, value);
            }
        }

        return properties;
    }
}
