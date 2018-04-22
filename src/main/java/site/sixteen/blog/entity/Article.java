package site.sixteen.blog.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="category_id")
    private Long categoryId;

    @Column(name = "tag_ids")
    private String tagIdStr;

    @NotBlank
    @Column
    private String title;

    @NotBlank
    @Column
    private String summary;

    @NotBlank
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String content;

    @Column(name = "read_count")
    private Integer readCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Transient
    private String userNicName;
    @Transient
    private String categoryName;
    @Transient
    private Tag[] tags;


    public void initDefaultInfo() {
        this.readCount=0;
        this.voteCount=0;
        this.commentCount=0;
        this.createTime= new Date();
    }

    public Article() {
    }

    public Article(Long id,Long userId, Long categoryId, String tagIdStr, String title, String summary, String content, Integer readCount, Integer commentCount, Integer voteCount, Integer status, Date createTime, Date updateTime) {
        this.id=id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.tagIdStr = tagIdStr;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.readCount = readCount;
        this.commentCount = commentCount;
        this.voteCount = voteCount;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                ", tagIdStr='" + tagIdStr + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", readCount=" + readCount +
                ", commentCount=" + commentCount +
                ", voteCount=" + voteCount +
                ", status=" + status +
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTagIdStr() {
        return tagIdStr;
    }

    public void setTagIdStr(String tagIdStr) {
        this.tagIdStr = tagIdStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getUserNicName() {
        return userNicName;
    }

    public void setUserNicName(String userNicName) {
        this.userNicName = userNicName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }


}
