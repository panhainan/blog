package site.sixteen.blog.properties;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@PropertySource(value = {"classpath:constant.yml"})
@Component
@ConfigurationProperties
public class ConstantProperties {

    private List<String> userFaceAllowType = new ArrayList<>();


    public List<String> getUserFaceAllowType() {
        return userFaceAllowType;
    }

    public void setUserFaceAllowType(List<String> userFaceAllowType) {
        this.userFaceAllowType = userFaceAllowType;
    }

}
