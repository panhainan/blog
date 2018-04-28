package site.sixteen.blog.entity;

import javax.persistence.*;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Boolean hot;

    public Tag() {
    }

    public Tag(String tagName, boolean hot) {
        this.name=tagName;
        this.hot= hot;
    }

    public Tag(String tagName) {
        this.name=tagName;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hot=" + hot +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHot() {
        return hot;
    }

    public void setHot(Boolean hot) {
        this.hot = hot;
    }
}
