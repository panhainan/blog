package site.sixteen.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.sixteen.blog.entity.Article;
import site.sixteen.blog.entity.Comment;
import site.sixteen.blog.entity.Tag;
import site.sixteen.blog.entity.User;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface BlogService {
    /**
     * 获取首页文章
     *
     * @param pageable
     * @return
     */
    Page<Article> getIndexArticles(Pageable pageable);

    /**
     * 根据ID获取文章信息
     *
     * @param id
     * @return
     */
    Article getArticle(long id);

    /**
     * 游客获取用户博客
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<Article> getUserArticles(Long userId, Pageable pageable);

    /**
     * 通过tag来获取文章
     *
     * @param tagName
     * @param pageable
     * @return
     */
    Page<Article> getArticles(String tagName, Pageable pageable);

    /**
     * 给文章点赞
     *
     * @param id
     * @return
     */
    boolean voteArticle(long id);

    /**
     * 游客评论文章
     *
     * @param comment
     * @return
     */
    boolean guestCommentArticle(Comment comment);
    /**
     * 用户评论文章
     *
     * @param comment
     * @return
     */
    boolean userCommentArticle(Comment comment);

    /**
     * 获取活跃用户
     * @param num 数量
     * @return
     */
    List<User> getActiveUser(int num);

    /**
     * 获取热门tag
     * @return
     */
    List<Tag> getHotTags();

    /**
     * 通过关键字查找文章
     * @param keyword
     * @param pageable
     * @return
     */
    Page<Article> searchArticles(String keyword, Pageable pageable);
}
