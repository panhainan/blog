package site.sixteen.blog.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户信息表
 *
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table(name = "user")
public class User {
    /**
     * 表主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 手机号 邮箱 用户名或第三方应用的唯一标识
     */
    @Column
    private String username;

    /**
     * 昵称
     */
    @Column
    private String nickname;

    /**
     * 性别
     */
    @Column
    private Integer gender;

    /**
     * 生日
     */
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 个性签名
     */
    @Column
    private String signature;

    /**
     * 绑定手机号
     */
    @Column
    private String mobile;

    /**
     * 手机号绑定的时间
     */
    @Column(name = "mobile_bind_time")
    private Date mobileBindTime;

    /**
     * 绑定邮箱号
     */
    @Column
    private String email;

    /**
     * 邮箱号绑定的时间
     */
    @Column(name = "email_bind_time")
    private Date emailBindTime;

    /**
     * 头像
     */
    @Column
    private String face;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", signature='" + signature + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobileBindTime=" + mobileBindTime +
                ", email='" + email + '\'' +
                ", emailBindTime=" + emailBindTime +
                ", face='" + face + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getMobileBindTime() {
        return mobileBindTime;
    }

    public void setMobileBindTime(Date mobileBindTime) {
        this.mobileBindTime = mobileBindTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getEmailBindTime() {
        return emailBindTime;
    }

    public void setEmailBindTime(Date emailBindTime) {
        this.emailBindTime = emailBindTime;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
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
}
