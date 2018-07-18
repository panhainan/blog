package site.sixteen.blog.controller;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.sixteen.blog.dto.ArticleArchiveDTO;
import site.sixteen.blog.entity.*;
import site.sixteen.blog.enums.GenerateValidCodeResult;
import site.sixteen.blog.exception.UserLoginException;
import site.sixteen.blog.exception.UserPasswordException;
import site.sixteen.blog.exception.UserRegisterException;
import site.sixteen.blog.properties.ConstantProperties;
import site.sixteen.blog.service.BlogService;
import site.sixteen.blog.service.UserService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Controller
public class UserController {

    @Autowired
    ConstantProperties constantProperties;

    @Value("${spring.http.multipart.location}")
    private String fileUploadLocation;

    @Value("${web.save-path}")
    private String savePath;


    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;


    @GetMapping("/register")
    public String register(Model model) {
        UserAuth userAuth = new UserAuth();
        model.addAttribute("userAuth", userAuth);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid UserAuth userAuth, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws UserRegisterException {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.register(userAuth);
        log.info("注册成功！");
        redirectAttributes.addFlashAttribute("successMsg", "注册成功！您可以登录了。");
        return "redirect:/login";
    }


    @GetMapping(value = "/login")
    public String login(Model model) {
        UserAuth userAuth = new UserAuth();
        model.addAttribute("userAuth", userAuth);
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid UserAuth userAuth, BindingResult bindingResult) throws UserLoginException {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        Subject currentSubject = SecurityUtils.getSubject();
        if (!currentSubject.isAuthenticated()) {
//            String hashAlgorithmName = "MD5";
//            Object credentials = userAuth.getPassword();
//            Object salt = ByteSource.Util.bytes(userAuth.getUsername());
//            int hashIterations = 2;
//            Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
//            userAuth.setPassword(String.valueOf(result));
            UsernamePasswordToken token = new UsernamePasswordToken(userAuth.getUsername(), userAuth.getPassword());
            try {
                // 执行登录.
                currentSubject.login(token);
            } catch (UnknownAccountException uae) {
                // 若没有指定的账户, 则 shiro 将会抛出 UnknownAccountException 异常.
                log.info("{}", uae.getMessage());
                throw new UserLoginException("用户名或密码错误！");
            } catch (IncorrectCredentialsException lae) {
                // 若账户存在, 但密码不匹配, 则 shiro 会抛出 IncorrectCredentialsException 异常。
                log.info("{}", lae.getMessage());
                throw new UserLoginException("用户名或密码错误！");
            } catch (LockedAccountException lae) {
                // 用户被锁定的异常 LockedAccountException
                log.info("{}", lae.getMessage());
                throw new UserLoginException("用户已被锁定！");
            } catch (AuthenticationException e) {
                // 所有认证时异常的父类.
                log.info("{}", e.getMessage());
                //登录失败
                throw new UserLoginException(e.getLocalizedMessage());
            }
        }
        return "redirect:/my";
    }



    /* 游客即可进行的操作 */
    @GetMapping({"/u/{username}","/u/{username}/blog"})
    public String getUserBlog(@PathVariable String username,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam(defaultValue = "new") String tab,
                              Model model) {
        User user = userService.getUser(username);
        model.addAttribute("user", user);
        model.addAttribute("activeSubTab", "blog");
        Sort sort;
        Pageable pageable;
        switch (tab) {
            case "hot":
                sort = new Sort(Sort.Direction.DESC,"readCount","voteCount","commentCount");
                pageable = new PageRequest(page - 1, size,sort);
                Page<Article> articlesByHot = blogService.getUserArticles(user.getId(),pageable);
                model.addAttribute("records",articlesByHot);
                break;
            case "category":
                List<Category> categoryList = userService.getUserCategories(username);
                model.addAttribute("categoryList",categoryList);
                break;
            case "archive":
                List<ArticleArchiveDTO> articleArchiveDTOList = userService.getUserArchives(username);
                model.addAttribute("articleArchiveDTOList",articleArchiveDTOList);
                break;
            default:
                tab="new";
                sort = new Sort(Sort.Direction.DESC,"updateTime","createTime");
                pageable = new PageRequest(page - 1, size,sort);
                Page<Article> articlesByNew = blogService.getUserArticles(user.getId(),pageable);
                model.addAttribute("records",articlesByNew);
        }
        model.addAttribute("activeSubSubTab", tab);
        return "user/front/user";
    }
    @GetMapping("/u/{username}/c/{categoryId}")
    @ResponseBody
    public List<Article> getUserCategoryArticles(@PathVariable String username,
                                          @PathVariable long categoryId){
        return userService.getUserCategoryArticles(username,categoryId);

    }
















    /* 需要登录后的操作 */

    @GetMapping("/my")
    public String my(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("activeNav", "user");
        log.info("{}", model);
        return "user/back/info";
    }

    @GetMapping("/my/info")
    public String myInfo(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        log.info("{}", model);
        return "user/back/info-edit";
    }

    @PostMapping("/my/info")
    public String myInfo(@RequestPart(required = false) MultipartFile file, User user, RedirectAttributes redirectAttributes) throws IOException {
        log.info("{}", user);
        if (!file.isEmpty()) {
            log.info("file type:{}", file.getContentType());
            if (constantProperties.getUserFaceAllowType().contains(file.getContentType())) {
                String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String fileName = user.getUsername() + suffixName;
                //保存图片
                file.transferTo(new File(fileName));
                String filePath = fileUploadLocation + "/" + fileName;
                File oldFile = new File(filePath);
                //按指定大小把图片进行缩和放（会遵循原图高宽比例）
                //此处把图片压成160的缩略图
                int width = 160, height = 160;
                Thumbnails.of(oldFile).size(width, height).toFile(oldFile);
                user.setFace(savePath+fileName);
            } else {
                log.info("文件类型错误！");
                redirectAttributes.addFlashAttribute("errorMsg", "头像修改失败，上传的文件文件类型不允许！");
            }
        }
        userService.updateMyInfo(user);
        return "redirect:/my";
    }

    @GetMapping("/my/email")
    public String myEmail(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        log.info("{}", model);
        return "user/back/email-edit";
    }

    @PostMapping("/my/email/code")
    @ResponseBody
    public Map<String, Object> myEmailValid(String email) {
        Map<String, Object> map = new HashMap<>(2);
        if (StringUtils.isEmpty(email)) {
            map.put("code", GenerateValidCodeResult.FAILED_EMAIL_NULL.value());
            map.put("msg", GenerateValidCodeResult.FAILED_EMAIL_NULL.msg());
        } else {
            GenerateValidCodeResult result = userService.generateEmailValidCode(email);
            map.put("code", result.value());
            map.put("msg", result.msg());
        }
        return map;
    }

    @PostMapping("/my/email/valid")
    public String myEmailValid(String email, String validCode, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(validCode)) {
            redirectAttributes.addFlashAttribute("emailBindSuccessMsg", "邮箱号和验证码都不能为空！");
        } else {
            boolean result = userService.validEmailCode(email, validCode);
            if (result) {
                redirectAttributes.addFlashAttribute("emailBindSuccessMsg", "邮箱绑定成功！");
            } else {
                redirectAttributes.addFlashAttribute("emailBindFailMsg", "邮箱绑定失败！");
            }
        }
        return "redirect:/my";
    }

    @GetMapping("/my/setting")
    public String mySetting() {
        return "user/back/setting";
    }

    @PostMapping("/my/setting")
    public String mySetting(String password, String newPassword, RedirectAttributes redirectAttributes) throws UserPasswordException {
        userService.updateMyPass(password, newPassword);
        redirectAttributes.addFlashAttribute("successMsg", "修改成功！");
        return "redirect:/my/setting";
    }

    @GetMapping("/my/logs")
    public String myLogs(@RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                         Model model) {
        Pageable pageable = new PageRequest(page - 1, size);
        Page<UserLog> logs = userService.getMyLogs(pageable);
        model.addAttribute("records", logs);
        return "user/back/logs";
    }

    @GetMapping("/my/articles")
    public String myArticles(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                             Model model) {
        Pageable pageable = new PageRequest(page - 1, size);
        Page<Article> articles = userService.getMyArticles(pageable);
        model.addAttribute("records", articles);
        return "user/back/articles";
    }

    @GetMapping("/my/article")
    public String goNewMyArticle(Model model) {
        List<Category> categoryList = userService.getMyCategories();
        model.addAttribute("categoryList", categoryList);
        return "user/back/article-new";
    }

    @PostMapping("/my/article")
    @ResponseBody
    public Map<String, Object> newMyArticle(@Valid Article article, BindingResult bindingResult) {
        Map<String, Object> map = new HashMap<>(1);
        if (bindingResult.hasErrors()) {
            map.put("code", 0);
            map.put("msg", "保存失败，数据不符合要求！");
        } else {
            userService.newMyArticle(article);
            map.put("code", 1);
            map.put("msg", "保存成功！");
        }
        return map;
    }

    @GetMapping("/my/article/update/{id}")
    public String goUpdateArticle(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        Article article = userService.getMyArticle(id);
        if (article == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "操作失败，你没有权限");
            return "redirect:/my/articles";
        }
        List<Category> categoryList = userService.getMyCategories();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("article", article);
        return "user/back/article-new";
    }

    @PostMapping("/my/article/delete")
    public String deleteArticle(long id, RedirectAttributes redirectAttributes) {
        if (!userService.deleteMyArticle(id)) {
            redirectAttributes.addFlashAttribute("errorMsg", "操作失败，你没有权限");
        } else {
            redirectAttributes.addFlashAttribute("successMsg", "删除成功！");
        }
        return "redirect:/my/articles";
    }

    @GetMapping("/my/comments")
    public String myComments(@RequestParam(defaultValue = "from")String type,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             Model model) {
        Pageable pageable = new PageRequest(page - 1, size);
        Page<Comment> comments;
        if("to".equals(type)){
            //获取我发表的评论
            comments = userService.getMyComments(pageable);
        }else{
            //获取我的文章的评论
            type="from";
            comments = userService.getMyArticleComments(pageable);
        }
        model.addAttribute("records",comments);
        model.addAttribute("type",type);
        return "user/back/comments";
    }

    @PostMapping("/my/comment/delete")
    public String deleteMyComment(Long id){
        userService.deleteMyComment(id);
        return  "redirect:/my/comments";
    }

    @GetMapping("/my/categories")
    public String myCategories(Model model) {
        List<Category> categoryList = userService.getMyCategories();
        model.addAttribute("categoryList", categoryList);
        return "user/back/categories";
    }

    @GetMapping("/my/category/{id}")
    @ResponseBody
    public List<Article> myCategoryArticles(@PathVariable long id) {
        return userService.getMyCategoryArticles(id);
    }

    @PostMapping("/my/category")
    public String saveOrUpdateMyCategory(@Valid Category category, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        boolean result;
        if (bindingResult.hasErrors()) {
            result = false;
        } else {
            result = userService.saveOrUpdateMyCategory(category);
        }
        if (result) {
            redirectAttributes.addFlashAttribute("code", 1);
            redirectAttributes.addFlashAttribute("msg", "操作成功！");
        } else {
            redirectAttributes.addFlashAttribute("code", 0);
            redirectAttributes.addFlashAttribute("msg", "操作失败！");
        }

        return "redirect:/my/categories";
    }

    @DeleteMapping("/my/category/{id}")
    @ResponseBody
    public Boolean deleteMyCategory(@PathVariable long id) {
        log.info("{}", id);
        return userService.deleteMyCategory(id);
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        SecurityUtils.getSubject().logout();
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return "redirect:/login";
    }

}