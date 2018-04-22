package site.sixteen.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.sixteen.blog.entity.*;
import site.sixteen.blog.enums.GenerateValidCodeResult;
import site.sixteen.blog.enums.ValidCodeType;
import site.sixteen.blog.exception.UserPasswordException;
import site.sixteen.blog.exception.UserRegisterException;
import site.sixteen.blog.repository.*;
import site.sixteen.blog.service.UserService;
import site.sixteen.blog.util.StringRandom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final int IDENTITY_TYPE_PHONE = 1;
    private static final int IDENTITY_TYPE_EMAIL = 2;
    private static final int IDENTITY_TYPE_USERNAME = 3;
    private static final int IDENTITY_TYPE_QQ = 4;
    private static final int IDENTITY_TYPE_WECHAT = 5;
    private static final int IDENTITY_TYPE_SINA = 6;


    /**
     * 验证码有效时长
     */
    private static final long VALID_CODE_EFFECTIVE_DURATION = 1000 * 60 * 10L;


    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLogRepository userLogRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserValidRepository userValidRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Value("${spring.mail.username}")
    private String Sender;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public Long register(UserAuth userAuth) throws UserRegisterException {
        if (checkUsernameIsExisted(userAuth.getUsername())) {
            throw new UserRegisterException("用户名已经存在！");
        }
        userAuth.setRegisterSource(IDENTITY_TYPE_USERNAME);
        userAuth.setDisabled(false);
        Date newDate = new Date();
        userAuth.setCreateTime(newDate);
        userAuth.setUpdateTime(newDate);
        userAuthRepository.save(userAuth);
        User user = new User();
        user.setUsername(userAuth.getUsername());
        user.setCreateTime(new Date());
        userRepository.save(user);
        return 1L;
    }

    @Override
    public User getCurrentUser() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        User user = getUserByUsername(username);
        return user;
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
        if (!subject.isAuthenticated()) {
            return;
        } else {
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


    private void sendMailValidCode(String toEmail, String validCode, String username, Date endTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Sender);
        message.setTo(toEmail);
        message.setSubject("博客邮箱验证邮件");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setText("您好，" + username + "，您的邮箱验证码是 " + validCode + " ,请尽快激活，有效期为 " + sdf.format(endTime) + " 。\n 如果您没有进行此操作请尽快登录您的账号修改密码，如果您没有博客账号请忽略此邮件。\n 谢谢！");
        mailSender.send(message);
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
        return categoryRepository.findAllByUserIdOrderById(currentUser.getId());
    }

    @Override
    public boolean saveOrUpdateMyCategory(Category category) {
        User currentUser = getCurrentUser();
        if (category.getId()==null) {
            //表示新建
            if (categoryRepository.existsByNameAndUserId(category.getName(), currentUser.getId())) {
                return false;
            }
        }
        //表示修改
        category.setUserId(currentUser.getId());
        categoryRepository.save(category);
        return true;
    }

    @Override
    public boolean deleteMyCategory(Long categoryId) {
        User currentUser = getCurrentUser();
        Category category = categoryRepository.findCategoryByIdAndUserId(categoryId, currentUser.getId());
        if (category != null && !category.getName().equals("默认分类")) {
            List<Article> list = articleRepository.findArticlesByCategoryId(categoryId);
            if(list==null || list.size()==0){
                categoryRepository.delete(categoryId);
            }else{
                Category defaultCategory = categoryRepository.findCategoryByName("默认分类");
                if(defaultCategory==null){
                    category.setName("默认分类");
                    categoryRepository.save(category);
                }else{
                    for(Article article: list){
                        article.setCategoryId(defaultCategory.getId());
                        articleRepository.save(article);
                    }
                    categoryRepository.delete(categoryId);
                }
            }
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

    private UserAuth getCurrentUserAuth() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        UserAuth userAuth = getUserAuthByUsername(username);
        return userAuth;
    }

    private boolean checkUsernameIsExisted(String username) {
        UserAuth userAuth = userAuthRepository.findUserAuthByUsername(username);
        if (userAuth != null) {
            return true;
        }
        return false;
    }

    @Override
    public UserAuth getUserAuthByUsername(String username) {
        return userAuthRepository.findUserAuthByUsername(username);
    }
}
