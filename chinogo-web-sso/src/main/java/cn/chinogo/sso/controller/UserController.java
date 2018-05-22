package cn.chinogo.sso.controller;

import cn.chinogo.base.service.NotifyUserService;
import cn.chinogo.constant.Const;
import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.TbAdminUser;
import cn.chinogo.pojo.TbUser;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.sso.service.UserService;
import cn.chinogo.utils.CookieUtils;
import cn.chinogo.utils.DateUtils;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户登录注册 Controller
 *
 * @author chinotan
 */
@Api("用户登录注册 Controller")
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(version = Const.CHINOGO_SSO_VERSION, timeout = 1000000)
    private UserService userService;

    @Reference(version = Const.CHINOGO_NOTIFY_VERSION, timeout = 1000000)
    private NotifyUserService notifyUserService;

    @Reference(version = Const.CHINOGO_REDIS_VERSION, timeout = 1000000)
    private JedisClient jedisClient;

    @Value("${user_not_exist}")
    private String USER_NOT_EXIST;

    @Value("${password_error}")
    private String PASSWORD_ERROR;

    @Value("${portal_path}")
    private String PORTAL_PATH;

    @Value("${redisKey.prefix.user_session}")
    private String USER_SESSION;

    @Value("${redisKey.expire_time}")
    private Integer EXPIRE_TIME;
    
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 获取uuid
     *
     * @return uuid
     */
    @ApiOperation("获取uuid")
    @GetMapping(value = "/uuid")
    public String showRegister() {
        return UUID.randomUUID().toString();
    }

    @ApiOperation("获取用户列表")
    @GetMapping(value = "/list")
    public List<HashMap<String, Object>> userList(Page<TbUser> page) {
        List<TbUser> tbUsers = userService.userList(page);
        List<HashMap<String, Object>> collect = tbUsers.parallelStream().map(r -> {
            HashMap<String, Object> o = new HashMap<>(4);
            o.put("username", r.getUsername());
            o.put("created", DateUtils.formatDate(r.getCreated()));
            o.put("phone", r.getPhone());
            o.put("id", r.getId());
            return o;
        }).collect(Collectors.toList());
        return collect;
    }

    @ApiOperation("获取用户数量")
    @GetMapping(value = "/count")
    public Object userCount() {
        Integer i = userService.userCount();
        return i;
    }

    @ApiOperation("上传用户头像")
    @PostMapping(value = "/upload")
    public Object userUpload(@RequestBody String uploadFile, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        Map<String, Object> map = new HashMap<>();
        map.put("result", "");

        if (user == null) {
            map.put("status", "-2");
            map.put("msg", "failed");
            return map;
        }

        Map res = FastJsonConvert.convertJSONToObject(uploadFile, Map.class);
        String uploadFile1 = (String) res.get("uploadFile");
        user.setAvatar(uploadFile1);
        user.setUsername(null);
        Boolean aBoolean = userService.updateUserAvatar(user);
        
        // redis中更新图片
        String key = USER_SESSION + token;
        jedisClient.set(key, FastJsonConvert.convertObjectToJSON(user));
        jedisClient.expire(key, EXPIRE_TIME * 4);

        if (!aBoolean) {
            map.put("status", "-2");
            map.put("msg", "failed");
            return map;
        } else {
            map.put("status", "1");
            map.put("msg", "suc");
            map.put("path", uploadFile1);
            return map;
        }
    }

    /**
     * 用户登录
     *
     * @param user     POJO
     * @param response
     * @param request
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public String login(@RequestBody TbUser user, HttpServletResponse response, HttpServletRequest request) {

        ChinogoResult result = userService.login(user);

        if (result.getStatus() == 200) {

            CookieUtils.setCookie(request, response, Const.TOKEN_LOGIN, result.getData().toString());

            return PORTAL_PATH;
        }

        if (result.getStatus() == 400) {

            return USER_NOT_EXIST;
        }

        if (result.getStatus() == 401) {

            return PASSWORD_ERROR;
        }

        return PASSWORD_ERROR;

    }

    /**
     * 管理员用户登录
     *
     * @param user     POJO
     * @return
     */
    @ApiOperation("管理员用户登录")
    @PostMapping(value = "/admin/login")
    public Object managerLogin(@RequestBody TbAdminUser user) {

        ChinogoResult result = userService.managerLogin(user);

        return result;

    }

    /**
     * 用户登出
     * 
     * @param token
     * @param response
     * @param request
     */
    @ApiOperation("用户登出")
    @PostMapping(value = "/logout")
    public void logout(@CookieValue(required = false, value = Const.TOKEN_LOGIN) String token, HttpServletResponse response, 
                       HttpServletRequest request) {

        // 在redis中清除用户信息
        userService.logout(token);
        
        // 清除用户cookie中的信息
        CookieUtils.deleteCookie(request, response, Const.TOKEN_LOGIN);
        
    }

    /**
     * 验证用户名、邮箱、电话是否重复
     *
     * @param isEngaged 检测的名称
     * @param regName   用户名
     * @param email     邮箱
     * @param phone     电话
     * @return
     */
    @ApiOperation("验证用户名、邮箱、电话是否重复")
    @PostMapping("/validateUser/{isEngaged}")
    public String validateUser(@PathVariable String isEngaged, @RequestParam(defaultValue = "") String regName, @RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "") String phone) {
        return userService.validateUser(isEngaged, regName, email, phone);
    }

    /**
     * 验证码判断
     *
     * @param authCode 判断验证码是否正确
     * @param uuid
     * @return
     */
    @ApiOperation("验证码判断")
    @RequestMapping("/validateAuthCode")
    public String validateAuthCode(String authCode, String uuid) {
        return userService.validateAuthCode(authCode, uuid);
    }

    /**
     * 发送手机验证码
     *
     * @param mobile 电话号码
     * @return
     */
    //http://localhost:8104/notifyuser/mobileCode?state=&mobile=%2B008615669970074&_=1486641954248
    @ApiOperation("发送手机验证码")
    @RequestMapping("/mobileCode")
    public String mobileCode(String mobile) {
        return notifyUserService.mobileNotify(mobile);
    }

    /**
     * 请求格式 POST
     *
     * @param regName    注册名
     * @param pwd        第一次密码
     * @param pwdRepeat  第二次密码
     * @param phone      电话
     * @param mobileCode 手机验证码
     * @param email      邮箱
     * @param authCode   输入的验证码
     * @param uuid       Redis验证码uuid
     * @return
     */
    @ApiOperation("注册")
    @RequestMapping("/regService")
    public ChinogoResult regService(String regName, String pwdRepeat, String pwd, String phone, String mobileCode,
                                    String uuid, String authCode, @RequestParam(required = false, value = "") String email) {
        return userService.register(regName, pwd, pwdRepeat, phone, mobileCode, uuid, authCode, email);
    }

    /**
     * 获取用户信息
     *
     * @param _cgt
     * @return
     */
    @ApiOperation("获取用户信息")
    @RequestMapping("/userInfo")
    public Object userInfo(@CookieValue(required = false, value = "_cgt") String _cgt) {
        Map<String, Object> map = new HashMap();
        if (!StringUtils.isBlank(_cgt)) {
            ChinogoResult chinogoResult = userService.token(_cgt);
            if (chinogoResult.isOK()) {
                String userString = (String) chinogoResult.getData();
                TbUser user = FastJsonConvert.convertJSONToObject(userString, TbUser.class);

                String avatar = user.getAvatar();
                String username = user.getUsername();

                map.put("status", "1");
                map.put("msg", "suc");

                Map<String, String> userMap = new HashMap();
                userMap.put("name", username);
                userMap.put("avatar", avatar);
                
                map.put("result", userMap);

                return map;
            }
        }
        map.put("status", "-1");
        map.put("msg", "未登录");
        map.put("result", "");

        return map;
    }

    /**
     * 获取当前的user
     * @param token
     * @return
     */
    private TbUser getUser(String token) {
        TbUser user = null;
        String userJson = null;

        if (StringUtils.isNoneEmpty(token)) {
            try {
                userJson = jedisClient.get(USER_SESSION + token);
            } catch (Exception e) {
                logger.error("Redis 错误", e);
            }

            if (StringUtils.isNoneEmpty(userJson)) {
                user = FastJsonConvert.convertJSONToObject(userJson, TbUser.class);
            }
        }

        return user;
    }
}
