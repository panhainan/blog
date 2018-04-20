package site.sixteen.blog.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table(name="user_valid")
public class UserValid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 所属账户
     */
    @Column
    private String username;
    /**
     * 验证码
     */
    @Column
    private String code;

    /**
     * 验证码类型：1 邮箱，2 手机
     */
    @Column(name = "code_type",length = 1)
    private Integer codeType;

    /**
     * 验证主体：邮箱，手机或
     */
    @Column(name = "valid_subject")
    private String validSubject;
    /**
     * 生成时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 失效时间
     */
    @Column(name = "end_time")
    private Date endTime;

    @Override
    public String toString() {
        return "UserValid{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", code='" + code + '\'' +
                ", codeType=" + codeType +
                ", validSubject='" + validSubject + '\'' +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getValidSubject() {
        return validSubject;
    }

    public void setValidSubject(String validSubject) {
        this.validSubject = validSubject;
    }
}
