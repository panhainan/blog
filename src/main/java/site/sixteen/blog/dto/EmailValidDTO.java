package site.sixteen.blog.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public class EmailValidDTO {


    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String validCode;


    @Override
    public String toString() {
        return "EmailValidDTO{" +
                "email='" + email + '\'' +
                ", validCode='" + validCode + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }
}
