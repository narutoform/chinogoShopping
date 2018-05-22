package cn.chinogo.sso.service.impl;

import cn.chinogo.constant.Const;
import cn.chinogo.mapper.TbAdminUserMapper;
import cn.chinogo.mapper.TbUserMapper;
import cn.chinogo.pojo.ChinogoResult;
import cn.chinogo.pojo.TbAdminUser;
import cn.chinogo.pojo.TbUser;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.sso.service.UserService;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * 用户登录相关服务 Service 实现
 *
 * @author chinotan
 */
@Service(version = Const.CHINOGO_SSO_VERSION, timeout = 1000000)
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String KEY = "success";
    public static final int ERROR = 1;
    public static final int SUCCESS = 0;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private TbAdminUserMapper userAdminMapper;

    @Reference(version = Const.CHINOGO_REDIS_VERSION)
    private JedisClient jedisClient;

    @Value("${redisKey.prefix.user_session}")
    private String USER_SESSION;

    @Value("${redisKey.expire_time}")
    private Integer EXPIRE_TIME;

    @Value("${login.validation.ispinengaged}")
    private String ISPINENGAGED;

    @Value("${login.validation.isemailengaged}")
    private String ISEMAILENGAGED;

    @Value("${login.validation.ismobileengaged}")
    private String ISMOBILEENGAGED;

    @Value("${login.random_number}")
    private Integer RANDOM_NUMBER;

    @Value("${redisKey.prefix.verifycode}")
    private String VERIFYCODE;

    @Value("${redisKey.prefix.mobile_login_code}")
    private String MOBILE_LOGIN_CODE;

    /**
     * 请求格式 GET
     * 注册数据校验
     *
     * @param data     校验数据
     * @param type     类型 可选参数1、2、3分别代表username、phone、email
     * @param callback 可选参数 有参表示jsonp调用
     * @return {
     * status: 200 //200 成功 400 参数错误 500 系统异常
     * msg: "OK" // 错误 参数错误
     * data: false // 返回数据，true：数据可用，false：数据不可用
     * }
     */
    @Override
    public ChinogoResult checkUserData(String data, Integer type, String callback) {

        if (StringUtils.isNotBlank(callback)) {
            return ChinogoResult.ok(callback);
        }

        Wrapper<TbUser> wrapper = new EntityWrapper<>();

        switch (type) {

            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                logger.error("type参数传递错误！");
                return ChinogoResult.build(400, "error", "参数错误");
        }

        List<TbUser> list = userMapper.selectList(wrapper);

        if (list != null && list.size() > 0) {
            return ChinogoResult.ok(false);
        }

        return ChinogoResult.ok(true);
    }

    /**
     * 请求格式 POST
     * 用户注册
     *
     * @param user Tbuser POJO Json
     * @return {
     * status: 200 //200 成功 400 数据错误 500 系统异常
     * msg: "OK" //错误 注册失败. 请校验数据后请再提交数据.
     * data: null
     * }
     */
    @Override
    public ChinogoResult register(TbUser user) {

        if (user == null) {
            return ChinogoResult.build(400, "error", "数据为空");
        }

        boolean usernameb = (boolean) checkUserData(user.getUsername(), ERROR, null).getData();
        boolean phoneb = (boolean) checkUserData(user.getPhone(), 2, null).getData();
        boolean emailb = (boolean) checkUserData(user.getEmail(), 3, null).getData();

        if (usernameb & phoneb & emailb) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

            user.setCreated(new Date());
            user.setUpdated(new Date());

            userMapper.insert(user);
            return ChinogoResult.ok();
        }

        return ChinogoResult.build(400, "error", "注册失败. 请校验数据后请再提交数据");


    }

    /**
     * 请求格式 POST
     * 用户登录
     *
     * @param user Tbuser POJO Json
     * @return {
     * status: 200 //200 成功 400 登录失败 500 系统异常
     * msg: "OK" //错误 用户名或密码错误,请检查后重试.
     * data: "fe5cb546aeb3ce1bf37abcb08a40493e" //登录成功，返回token
     * }
     */

    @Override
    public ChinogoResult login(TbUser user) {

        if (user == null) {
            return ChinogoResult.build(400, "error", "数据为空");
        }

        Wrapper<TbUser> wrapper = new EntityWrapper<>();

        wrapper.eq("username", user.getUsername());

        List<TbUser> list = userMapper.selectList(wrapper);

        if (list == null || list.size() == 0) {
            return ChinogoResult.build(400, "用户名不存在");
        }

        TbUser check = list.get(0);

        if (!check.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
            return ChinogoResult.build(401, "用户名或密码错误");
        }

        TbUser result = new TbUser();

        result.setUsername(check.getUsername());
        result.setAvatar(check.getAvatar());
        result.setId(check.getId());

        String token = UUID.randomUUID().toString().replaceAll("-", "");

        String key = USER_SESSION + token;
        jedisClient.set(key, FastJsonConvert.convertObjectToJSON(result));

        jedisClient.expire(key, EXPIRE_TIME * 4);

        return ChinogoResult.ok(token);
    }

    @Override
    public ChinogoResult managerLogin(TbAdminUser user) {
        if (user == null) {
            return ChinogoResult.build(400, "error", "数据为空");
        }

        Wrapper<TbAdminUser> wrapper = new EntityWrapper<>();

        wrapper.eq("username", user.getUsername());

        List<TbAdminUser> list = userAdminMapper.selectList(wrapper);

        if (list == null || list.size() == 0) {
            return ChinogoResult.build(400, "用户名不存在");
        }

        TbAdminUser check = list.get(0);

        if (!check.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
            return ChinogoResult.build(401, "用户名或密码错误");
        }

        TbAdminUser result = new TbAdminUser();

        result.setUsername(check.getUsername());
        result.setAvatar(check.getAvatar());
        result.setId(check.getId());

        return ChinogoResult.ok(result);
    }

    /**
     * 请求格式 GET
     * 根据token值获取用户信息
     *
     * @param token token值
     * @return {
     * status: 200 //200 成功 400 没有此token 500 系统异常
     * msg: "OK" //错误 没有此token.
     * data: {"username":"chinogo","id":"id"} //返回用户名
     * }
     */
    @Override
    public ChinogoResult token(String token) {
        try {
            String user = jedisClient.get(USER_SESSION + token);

            if (StringUtils.isNotBlank(user)) {

                return ChinogoResult.ok(user);
            }

        } catch (Exception e) {

            logger.error("Redis服务出错");

        }

        return ChinogoResult.build(400, "没有此用户");
    }

    /**
     * 请求格式 GET
     * 根据token值 退出登录
     *
     * @param token token值
     * @return {
     * status: 200 //200 成功 400 没有此token 500 系统异常
     * msg: "OK" //错误 没有此token.
     * data: null
     * }
     */
    @Override
    public ChinogoResult logout(String token) {

        try {
            jedisClient.del(USER_SESSION + token);
        } catch (Exception e) {
            logger.error("没有登录", e);

            return ChinogoResult.build(400, "没有登录");
        }

        return ChinogoResult.ok();
    }

    /**
     * 请求格式 POST
     * 注册检查是否可用
     *
     * @param isEngaged 需要检查是否使用的名称
     * @return {
     * "success": 0 可用 1 不可用
     * "morePin":["sssss740","sssss5601","sssss76676"] //isEngaged = isPinEngaged时返回推荐
     * }
     */
    @Override
    public String validateUser(String isEngaged, String regName, String email, String phone) {

        Random random = new Random();

        HashMap<String, Object> map = new HashMap<>();

        Wrapper<TbUser> wrapper = new EntityWrapper<>();

        if (StringUtils.isNotBlank(isEngaged)) {

            if (isEngaged.equals(ISPINENGAGED) && StringUtils.isNotBlank(regName)) {

                wrapper.eq("username", regName);

                List<TbUser> users = userMapper.selectList(wrapper);

                if (users == null || users.size() == 0) {
                    //用户名 可用
                    map.put(KEY, 0);

                    return FastJsonConvert.convertObjectToJSON(map);
                }

                //用户名 不可用
                map.put(KEY, ERROR);
                ArrayList<String> morePin = new ArrayList<>();
                morePin.add(regName + random.nextInt(RANDOM_NUMBER));
                morePin.add(regName + random.nextInt(RANDOM_NUMBER));
                morePin.add(regName + random.nextInt(RANDOM_NUMBER));
                // 不考虑生成的用户名继续重名
                map.put("morePin", morePin);

                return FastJsonConvert.convertObjectToJSON(map);

            } else {
                if (isEngaged.equals(ISEMAILENGAGED) && StringUtils.isNotBlank(email)) {

                    wrapper.eq("email", email);

                    List<TbUser> users = userMapper.selectList(wrapper);

                    if (users == null || users.size() == 0) {
                        //email 可用
                        map.put(KEY, 0);

                        return FastJsonConvert.convertObjectToJSON(map);
                    }
                    //email 不可用
                    map.put(KEY, ERROR);

                    return FastJsonConvert.convertObjectToJSON(map);


                } else if (isEngaged.equals(ISMOBILEENGAGED) && StringUtils.isNotBlank(phone)) {

                    wrapper.eq("phone", phone);

                    List<TbUser> users = userMapper.selectList(wrapper);

                    if (users == null || users.size() == 0) {
                        //phone 可用
                        map.put(KEY, 0);

                        return FastJsonConvert.convertObjectToJSON(map);
                    }
                    //phone 不可用
                    map.put(KEY, ERROR);

                    return FastJsonConvert.convertObjectToJSON(map);
                }


            }

        }

        logger.error("传递类型出错！");
        map.put("error", ERROR);

        return FastJsonConvert.convertObjectToJSON(map);
    }

    /**
     * 请求格式 POST
     * 验证验证码
     *
     * @param authCode 输入的验证码
     * @param uuid     Redis验证码uuid
     * @return {
     * "success": 0 可用 1 不可用
     * }
     */
    @Override
    public String validateAuthCode(String authCode, String uuid) {

        HashMap<String, Integer> map = new HashMap<>();

        try {
            String redisAuthCode = jedisClient.get(VERIFYCODE + uuid);

            if (StringUtils.isBlank(redisAuthCode)) {

                map.put(KEY, ERROR);

                logger.info("Redis中根据key查询不到");

                return FastJsonConvert.convertObjectToJSON(map);
            }

            if (StringUtils.isBlank(authCode)) {
                map.put(KEY, ERROR);

                logger.info("验证码为空");

                return FastJsonConvert.convertObjectToJSON(map);
            }

            if (redisAuthCode.equalsIgnoreCase(authCode)) {

                map.put(KEY, SUCCESS);

                return FastJsonConvert.convertObjectToJSON(map);
            }


        } catch (Exception e) {

            logger.error("redis 服务出错", e);

        }

        map.put(KEY, ERROR);

        return FastJsonConvert.convertObjectToJSON(map);
    }

    /**
     * 请求格式 POST
     * 注册
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
    @Override
    public ChinogoResult register(String regName, String pwd, String pwdRepeat, String phone, String mobileCode, String uuid, String authCode, String email) {

        if (!pwd.equals(pwdRepeat)) {

            String info = "两次密码不正确";
            return ChinogoResult.build(406, info);
        }

        if (StringUtils.isNotBlank(authCode)) {

            String code = "";
            code = jedisClient.get(VERIFYCODE + uuid);
            //if (StringUtils.isBlank(code)) {
            //    String info = "验证码不正确或已过期，请重新获取";
            //    String convert = ConvertUtils.convert(info);
            //    return "({'info':'" + convert + "'})";
            //}

            if (StringUtils.isBlank(code) || !code.equalsIgnoreCase(authCode)) {

                String info = "验证码不正确或已过期，请重新获取";
                return ChinogoResult.build(406, info);

            }

        } else {
            String info = "验证码不能为空";
            return ChinogoResult.build(400, info);
        }

        if (StringUtils.isNotBlank(phone)) {
            String phonecode = "";

            phonecode = jedisClient.get(MOBILE_LOGIN_CODE + phone);

            if (StringUtils.isBlank(phonecode) || !phonecode.equals(mobileCode)) {

                String info = "短信验证码不正确或已过期,请重新获取";
                return ChinogoResult.build(406, info);

            }
        } else {
            String info = "手机号码不能为空";
            return ChinogoResult.build(406, info);
        }

        if (StringUtils.isNotBlank(regName)) {

            TbUser user = new TbUser();
            user.setUsername(regName);
            user.setPassword(DigestUtils.md5DigestAsHex(pwd.getBytes()));
            user.setPhone(phone);

            user.setCreated(new Date());
            user.setUpdated(new Date());

            if (StringUtils.isNotBlank(email)) {
                user.setEmail(email);
            }

            userMapper.insert(user);
            //注册成功
            return ChinogoResult.ok();
        }
        //注册失败
        return ChinogoResult.build(500, "注册失败");
    }

    @Override
    public List<TbUser> userList(Page<TbUser> page) {
        Wrapper<TbUser> wrapper = new EntityWrapper<>();
        wrapper.orderBy("created", false);
        RowBounds rowBounds = new RowBounds((page.getCurrent() - 1) * page.getSize(), page.getSize());
        List<TbUser> list = userMapper.selectPage(rowBounds, wrapper);
        return list;
    }

    @Override
    public Integer userCount() {
        Wrapper<TbUser> wrapper = new EntityWrapper<>();
        Integer integer = userMapper.selectCount(wrapper);
        return integer;
    }

    /**
     * 插入用户头像
     *
     * @param user
     * @return
     */
    @Override
    public Boolean updateUserAvatar(TbUser user) {
        Integer u = userMapper.updateById(user);
        
        return u > 0;
    }
}
