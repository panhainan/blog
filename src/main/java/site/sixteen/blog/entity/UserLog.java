package site.sixteen.blog.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table(name="user_log")
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column
    private String ip;
    @Column
    private String address;
    @Column(name = "log_time")
    private Date logTime;
    @Column
    private String device;


    @Override
    public String toString() {
        return "UserLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", address='" + address + '\'' +
                ", logTime=" + logTime +
                ", device='" + device + '\'' +
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
