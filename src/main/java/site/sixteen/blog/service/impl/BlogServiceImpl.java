package site.sixteen.blog.service.impl;

import com.youbenzi.mdtool.tool.MDTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.sixteen.blog.entity.*;
import site.sixteen.blog.repository.ArticleRepository;
import site.sixteen.blog.repository.CategoryRepository;
import site.sixteen.blog.repository.CommentRepository;
import site.sixteen.blog.service.BlogService;

import java.util.Date;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Service
public class BlogServiceImpl extends CommonService implements BlogService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<Article> getIndexArticles(Pageable pageable) {
        Page<Article> articlePage = articleRepository.findArticlesByStatus(1, pageable);
        setArticleListAssociatedInfo(articlePage.getContent());
        return articlePage;
    }

    @Override
    public Article getArticle(long articleId) {
        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return null;
        }
        //未发布状态不允许查看
        if (article.getStatus() != 1) {
            return null;
        }
        article.setReadCount(article.getReadCount() + 1);
        articleRepository.save(article);
        List<Comment> commentList = commentRepository.findAllByArticleIdOrderByCreateTime(articleId);
        article.setCommentList(commentList);
        Category category = categoryRepository.findOne(article.getCategoryId());
        article.setCategoryName(category.getName());
        setArticleTags(article);
        setArticleUserInfo(article);
        return article;
    }

    @Override
    public Page<Article> getUserArticles(Long userId, Pageable pageable) {
        return articleRepository.findArticlesByUserIdAndStatus(userId, 1, pageable);
    }

    @Override
    public Page<Article> getArticles(String tagName, Pageable pageable) {
        Tag tag = tagRepository.findTagByName(tagName);
        if (tag == null) {
            return null;
        }
        String tagStr = "%;" + tag.getId() + ";%";
        Page<Article> articlePage = articleRepository.findArticlesByTagIdStrLikeAndStatusOrderByCreateTimeDesc(tagStr, 1, pageable);
        setArticleListAssociatedInfo(articlePage.getContent());
        return articlePage;
    }

    @Override
    public boolean voteArticle(long id) {
        Article article = articleRepository.findOne(id);
        if (article == null) {
            return false;
        } else {
            article.setVoteCount(article.getVoteCount() + 1);
            articleRepository.save(article);
            return true;
        }
    }

    @Override
    public boolean guestCommentArticle(Comment comment) {
        Article article = articleRepository.findOne(comment.getArticleId());
        if (article == null) {
            return false;
        } else {
            comment.setCreateTime(new Date());
            comment.setSignIn(false);
            comment.setVoteCount(0);
            comment.setArticleName(article.getTitle());
            commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount() + 1);
            articleRepository.save(article);
            return true;
        }
    }

    @Override
    public boolean userCommentArticle(Comment comment) {
        Article article = articleRepository.findOne(comment.getArticleId());
        if (article == null) {
            return false;
        } else {
            User user = getCurrentUser();
            if (user == null) {
                return false;
            }
            comment.setNickname(user.getNickname());
            comment.setWebsite("/u/"+user.getUsername());
            comment.setUserId(user.getId());
            comment.setSignIn(true);
            comment.setCreateTime(new Date());
            comment.setVoteCount(0);
            comment.setArticleName(article.getTitle());
            commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount() + 1);
            articleRepository.save(article);
            return true;
        }
    }

    @Override
    public List<User> getActiveUser(int num) {
        return userRepository.findActiveUsers(num);
    }

    @Override
    public List<Tag> getHotTags() {
        //获取hot=true的tag
        return tagRepository.findTagsByHotOrderByIdDesc(true);
    }

    @Override
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        String keywordLike = "%" + keyword + "%";
        Tag tag = tagRepository.findTagByName(keyword);
        Page<Article> articlePage;
        if (tag == null) {
            articlePage = articleRepository.findArticlesByTitleLikeOrSummaryLike(keywordLike, keywordLike, pageable);
        } else {
            String tagIdStr = "%;" + tag.getId() + ";%";
            articlePage = articleRepository.findArticlesByTitleLikeOrSummaryLikeOrTagIdStrLike(keywordLike, keyword, tagIdStr, pageable);
        }
        setArticleListAssociatedInfo(articlePage.getContent());
        return articlePage;
    }

}
