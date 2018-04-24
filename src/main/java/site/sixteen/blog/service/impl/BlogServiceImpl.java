package site.sixteen.blog.service.impl;

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
        if(article==null){
            return null;
        }
        //未发布状态不允许查看
        if (article.getStatus() != 1) {
            return null;
        }
        article.setReadCount(article.getReadCount()+1);
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
        String tagStr = "%;"+tag.getId()+";%";
        Page<Article> articlePage = articleRepository.findArticlesByTagIdStrLikeAndStatusOrderByCreateTimeDesc(tagStr, 1, pageable);
        setArticleListAssociatedInfo(articlePage.getContent());
        return articlePage;
    }

    @Override
    public boolean voteArticle(long id) {
        Article article = articleRepository.findOne(id);
        if(article==null){
            return false;
        }else{
            article.setVoteCount(article.getVoteCount()+1);
            articleRepository.save(article);
            return true;
        }
    }

    @Override
    public boolean guestCommentArticle(Comment comment) {
        Article article = articleRepository.findOne(comment.getArticleId());
        if(article==null){
            return false;
        }else{
            comment.setCreateTime(new Date());
            comment.setSignIn(false);
            comment.setVoteCount(0);
            commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount()+1);
            articleRepository.save(article);
            return true;
        }
    }
    @Override
    public boolean userCommentArticle(Comment comment) {
        Article article = articleRepository.findOne(comment.getArticleId());
        if(article==null){
            return false;
        }else{
            User user = getCurrentUser();
            if(user == null){
                return false;
            }
            comment.setNickname(user.getNickname());
            //TODO 设置用户主页地址

            comment.setUserId(user.getId());
            comment.setSignIn(true);
            comment.setCreateTime(new Date());
            comment.setVoteCount(0);
            commentRepository.save(comment);
            article.setCommentCount(article.getCommentCount()+1);
            articleRepository.save(article);
            return true;
        }
    }

}
