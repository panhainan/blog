package site.sixteen.blog.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler({UserRegisterException.class})
    public String handleUserRegisterException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", e.getLocalizedMessage());
        return "redirect:/register";
    }

    @ExceptionHandler({UserPasswordException.class})
    public String handleUserPasswordException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", e.getLocalizedMessage());
        return "redirect:/my/setting";
    }
    @ExceptionHandler({UserLoginException.class})
    public String handlerUserLoginException(Exception e,RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", e.getLocalizedMessage());
        return "redirect:/login";
    }
}
