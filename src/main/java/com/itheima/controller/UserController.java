package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JwtUtil;
import com.itheima.utils.Md5Util;
import com.itheima.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.itheima.pojo.Result.success;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {

        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {//没有被占用
            userService.register(username, password);
            return success();
        } else {
            return Result.error("用户名已被占用");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {

        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断用户是否存在
        if (loginUser == null) {
            return Result.error("用户名不存在");
        }

        //判断密码是否正确 loginUser对象中的password是加密后的密文，需加密再比较
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", loginUser.getUsername());
            claims.put("id", loginUser.getId());//键值对的形式存储数据
            ThreadLocalUtil.set(claims);
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);

            return success(token);
        }
        return Result.error("密码错误");
    }

    //获取用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name = "Authorization") String token*/) {
        //根据用户名查询用户
            /*Map<String, Object> map = JwtUtil.parseToken(token);
            String username = (String)map.get("username");*/

        //从ThreadLocal中获取数据
        Map<String, Object> map = ThreadLocalUtil.get();
        if(map == null){
            throw new IllegalStateException("ThreadLocal map is not set111");
        }
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        return success(user);
    }

    //更新用户信息
    @PutMapping("/update")
    //@Validated注解表示开启校验
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return success();
    }

    //更新头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        Map<String, Object> map = ThreadLocalUtil.get();
        if (map == null){
            throw new IllegalStateException("ThreadLocal map is not set");
        }
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);

        userService.updateAvatar(user, avatarUrl);

        return Result.success(user);
    }

    //更新密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");//确认密码

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码，再和old_pwd比对
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }

        //newPwd和rePwd是否一致
        if(!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一致");
        }

        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        //删除redis对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

}

