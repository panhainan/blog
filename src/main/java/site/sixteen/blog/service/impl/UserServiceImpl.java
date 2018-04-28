package site.sixteen.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.sixteen.blog.dto.ArticleArchiveDTO;
import site.sixteen.blog.entity.*;
import site.sixteen.blog.enums.GenerateValidCodeResult;
import site.sixteen.blog.enums.IdentityType;
import site.sixteen.blog.enums.ValidCodeType;
import site.sixteen.blog.exception.UserPasswordException;
import site.sixteen.blog.exception.UserRegisterException;
import site.sixteen.blog.repository.*;
import site.sixteen.blog.service.UserService;
import site.sixteen.blog.util.StringRandom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Service
public class UserServiceImpl extends CommonService implements UserService {


    @Autowired
    private UserLogRepository userLogRepository;
    @Autowired
    private UserValidRepository userValidRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;


    @Override
    public User getUserByUsername(String username) {
        return super.getUserByUsername(username);
    }

    @Override
    public Long register(UserAuth userAuth) throws UserRegisterException {
        if (checkUsernameIsExisted(userAuth.getUsername())) {
            throw new UserRegisterException("用户名已经存在！");
        }
        userAuth.setRegisterSource(IdentityType.USERNAME.value());
        userAuth.setDisabled(false);
        Date newDate = new Date();
        userAuth.setCreateTime(newDate);
        userAuth.setUpdateTime(newDate);
        userAuthRepository.save(userAuth);
        User user = new User();
        user.setUsername(userAuth.getUsername());
        user.setNickname(user.getUsername());
        user.setCreateTime(new Date());
        userRepository.save(user);
        return 1L;
    }

    @Override
    public User getUser(String username) {
        User user = userRepository.findUserByUsername(username);
        user.setEmail(null);
        user.setEmailBindTime(null);
        user.setMobile(null);
        user.setMobileBindTime(null);
        user.setUpdateTime(null);
        return user;
    }

    @Override
    public UserAuth getUserAuthByUsername(String username) {
        return super.getUserAuthByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        return super.getCurrentUser();
    }


    @Override
    public Long updateMyInfo(User user) {
        User dbUser = getCurrentUser();
        dbUser.setNickname(user.getNickname());
        dbUser.setGender(user.getGender());
        dbUser.setBirthday(user.getBirthday());
        dbUser.setSignature(user.getSignature());
        //TODO 需要专门针对手机进行绑定，目前不支持
        if (!StringUtils.isEmpty(user.getMobile())) {
            dbUser.setMobile(user.getMobile());
            dbUser.setMobileBindTime(new Date());
        }
        if (!StringUtils.isEmpty(user.getFace())) {
            dbUser.setFace(user.getFace());
        }
        dbUser.setUpdateTime(new Date());
        userRepository.saveAndFlush(dbUser);
        return 1L;
    }

    @Override
    public Long updateMyPass(String password, String newPassword) throws UserPasswordException {
        UserAuth dbUserAuth = getCurrentUserAuth();
        if (!dbUserAuth.getPassword().equals(password)) {
            throw new UserPasswordException("原密码不正确！");
        }
        dbUserAuth.setPassword(newPassword);
        userAuthRepository.save(dbUserAuth);
        return 1L;
    }

    @Override
    public void logUserLogin(UserLog userLog) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Session session = subject.getSession(true);
            log.info("{}", session);
            userLog.setUsername(String.valueOf(subject.getPrincipal()));
            userLog.setLogTime(new Date());
            userLog.setIp(session.getHost());
            userLogRepository.save(userLog);
        }
    }

    @Override
    public Page<UserLog> getMyLogs(Pageable pageable) {
        Subject subject = SecurityUtils.getSubject();
        String username = String.valueOf(subject.getPrincipal());
        return userLogRepository.findUserLogsByUsernameOrderByLogTimeDesc(username, pageable);
    }

    @Override
    public Page<Article> getMyArticles(Pageable pageable) {
        User user = getCurrentUser();
        Page<Article> articlePage = articleRepository.findArticlesByUserIdOrderByCreateTimeDesc(user.getId(), pageable);
        for (Article article : articlePage.getContent()) {
            Category category = categoryRepository.findOne(article.getCategoryId());
            article.setCategoryName(category.getName());
            setArticleTags(article);
        }
        return articlePage;
    }


    @Override
    public void newMyArticle(Article article) {
        User currentUser = getCurrentUser();
        String tagIdStr = article.getTagIdStr();
        //处理标签-保存到数据库
        article.setTagIdStr(saveTags(tagIdStr));
        //处理分类
        if (article.getCategoryId() == null || article.getCategoryId() == 0) {
            article.setCategoryId(getDefaultCategory(currentUser.getId()));
        }
        article.setUserId(currentUser.getId());
        //处理其他默认信息
        article.initDefaultInfo();
        articleRepository.save(article);
    }

    @Override
    public Article getMyArticle(long id) {
        Article article = articleRepository.findOne(id);
        User user = getCurrentUser();
        if (user.getId().longValue() == article.getUserId().longValue()) {
            setArticleTags(article);
            return article;
        }
        return null;
    }

    @Override
    public boolean deleteMyArticle(long id) {
        User user = getCurrentUser();
        Article article = articleRepository.findOne(id);
        if (user.getId().longValue() == article.getUserId().longValue()) {
            commentRepository.deleteCommentsByArticleId(id);
            articleRepository.delete(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Category> getUserCategories(String username) {
        User user = userRepository.findUserByUsername(username);
        return categoryRepository.findAllByUserIdOrderByIdDesc(user.getId());
    }

    @Override
    public List<Article> getUserCategoryArticles(String username, long categoryId) {
        return articleRepository.findArticlesByCategoryIdAndStatus(categoryId, 1);
    }

    @Override
    public List<ArticleArchiveDTO> getUserArchives(String username) {
        User user = userRepository.findUserByUsername(username);
        List<Article> articles = articleRepository.findArticlesByUserIdAndStatusOrderByCreateTimeDesc(user.getId(), 1);
        List<ArticleArchiveDTO> articleArchiveDTOList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Article article : articles) {
            Date createTime = article.getCreateTime();
            calendar.setTime(createTime);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            boolean flag = false;
            for (ArticleArchiveDTO a : articleArchiveDTOList) {
                if (a.getYear()== year && a.getMonth() == month) {
                    //list中已经存在对应年月
                    a.getArticles().add(article);
                    flag = true;
                }
            }
            if (!flag) {
                ArticleArchiveDTO articleArchiveDTO = new ArticleArchiveDTO(year, month);
                List<Article> articles1 = new ArrayList<>();
                articles1.add(article);
                articleArchiveDTO.setArticles(articles1);
                articleArchiveDTOList.add(articleArchiveDTO);
            }
        }
        return articleArchiveDTOList;
    }

    @Override
    public Page<Comment> getMyComments(Pageable pageable) {
        User user = getCurrentUser();
        return commentRepository.findCommentsByUserIdOrderByCreateTimeDesc(user.getId(), pageable);
    }

    @Override
    public Page<Comment> getMyArticleComments(Pageable pageable) {
        User user = getCurrentUser();
        List<Long> listIds = articleRepository.findArticleIdsByUserIdAndStatus(user.getId(), 1);
        if (listIds != null && listIds.size() > 0) {
            return commentRepository.findCommentsByArticleIdIn(listIds, pageable);
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteMyComment(Long id) {
        User user = getCurrentUser();
        Comment comment = commentRepository.findOne(id);
        if (comment == null || comment.getUserId() == null || id == null || comment.getUserId().longValue() != user.getId().longValue()) {
            return false;
        } else {
            commentRepository.delete(id);
            return true;
        }
    }


    @Override
    public GenerateValidCodeResult generateEmailValidCode(String email) {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            if (user.getUsername().equals(username)) {
                return GenerateValidCodeResult.FAILED_EMAIL_NOT_CHANGE;
            } else {
                return GenerateValidCodeResult.FAILED_EMAIL_IS_USED;
            }
        }
        Date now = new Date();
        List<UserValid> userValidList = userValidRepository.findAllByUsernameAndValidSubjectAndAndCodeTypeAndEndTimeGreaterThanEqual(username, email, ValidCodeType.EMAIL.value(), now);
        if (!userValidList.isEmpty()) {
            return GenerateValidCodeResult.FAILED_REPEAT_SEND;
        }
        String validCode = StringRandom.generateRandomStr(StringRandom.SIX_DIGITS);
        UserValid userValid = new UserValid();
        userValid.setCode(validCode);
        userValid.setCodeType(ValidCodeType.EMAIL.value());
        Date createTime = new Date();
        Date endTime = new Date(createTime.getTime() + VALID_CODE_EFFECTIVE_DURATION);
        userValid.setCreateTime(new Date());
        userValid.setEndTime(endTime);
        userValid.setUsername(username);
        userValid.setValidSubject(email);
        userValidRepository.save(userValid);
        sendMailValidCode(email, validCode, username, endTime);
        return GenerateValidCodeResult.SUCCESS;
    }

    @Override
    public boolean validEmailCode(String email, String validCode) {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        Date now = new Date();
        List<UserValid> userValidList = userValidRepository.findAllByUsernameAndValidSubjectAndAndCodeTypeAndEndTimeGreaterThanEqual(username, email, ValidCodeType.EMAIL.value(), now);
        boolean flag = false;
        for (UserValid userValid : userValidList) {
            if (userValid.getCode().equals(validCode)) {
                flag = true;
                User dbUser = userRepository.findUserByUsername(username);
                dbUser.setEmail(email);
                dbUser.setEmailBindTime(now);
                userRepository.save(dbUser);
                break;
            }
        }
        return flag;
    }

    @Override
    public List<Category> getMyCategories() {
        User currentUser = getCurrentUser();
        return categoryRepository.findAllByUserIdOrderByIdDesc(currentUser.getId());
    }

    @Override
    public boolean saveOrUpdateMyCategory(Category category) {
        User currentUser = getCurrentUser();
        if (category.getId() == null) {
            //表示新建
            if (categoryRepository.existsByNameAndUserId(category.getName().trim(), currentUser.getId())) {
                return false;
            }
        }
        //表示修改
        category.setUserId(currentUser.getId());
        category.setName(category.getName().trim());
        categoryRepository.save(category);
        return true;
    }

    @Override
    public boolean deleteMyCategory(Long categoryId) {
        User currentUser = getCurrentUser();
        Category category = categoryRepository.findCategoryByIdAndUserId(categoryId, currentUser.getId());
        if (category != null && !category.getName().equals(DEFAULT_CATEGORY_NAME)) {
            categoryRepository.delete(categoryId);
            List<Article> list = articleRepository.findArticlesByCategoryId(categoryId);
            if (list != null && list.size() > 0) {
                long defaultCategoryId = getDefaultCategory(currentUser.getId());
                for (Article article : list) {
                    article.setCategoryId(defaultCategoryId);
                    articleRepository.save(article);
                }
            }
            categoryRepository.delete(categoryId);
            return true;
        }
        return false;
    }

    @Override
    public List<Article> getMyCategoryArticles(Long categoryId) {
        User currentUser = getCurrentUser();
        Category dbCategory = categoryRepository.findCategoryByIdAndUserId(categoryId, currentUser.getId());
        if (dbCategory != null) {
            return articleRepository.findArticlesByCategoryId(categoryId);
        } else {
            return null;
        }
    }


}
