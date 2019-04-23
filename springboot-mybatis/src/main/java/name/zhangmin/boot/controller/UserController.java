package name.zhangmin.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import name.zhangmin.boot.model.User;
import name.zhangmin.boot.service.UserService;

/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */
@Controller
@RequestMapping(value = "/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private A a;

    @Autowired
    private C c;

    @ResponseBody
    @RequestMapping(value = "/add", produces = {"application/json;charset=UTF-8"})
    public int addUser(User user){
        return userService.addUser(user);
    }
    
    @ResponseBody
    @RequestMapping(value = "", produces = {"application/json;charset=UTF-8"})
    public User getUser(@RequestParam("userId") Integer userId)
    {
        a.say();
        c.say();
        return userService.getUserById(userId);
    }
}
