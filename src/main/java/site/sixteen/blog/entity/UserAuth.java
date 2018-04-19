package site.sixteen.blog.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 用户授权信息
 *
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table(name = "user_auth")
public class UserAuth {

    /**
     * 表主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 注册来源：1手机号 2邮箱 3用户名 4qq 5微信 6新浪微博
     * TODO 暂时默认用户名注册
     */
    @Column
    private Integer registerSource;

    /**
     * 手机号 邮箱 用户名或第三方应用的唯一标识
     */
    @Column
    @NotNull
    @Length(min = 3,max = 16)
    private String username;

    /**
     * 密码凭证(站内的保存密码，站外的不保存或保存token)
     */
    @Column
    @NotNull
    @Length(min = 6,max = 16)
    private String password;

    /**
     * 绑定时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新绑定时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 是否禁用：0未被禁用，1已被禁用
     */
    @Column
    private Boolean disabled;

    @Override
    public String toString() {
        return "UserAuth{" +
                "id=" + id +
                ", registerSource=" + registerSource +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", disabled=" + disabled +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(Integer registerSource) {
        this.registerSource = registerSource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
