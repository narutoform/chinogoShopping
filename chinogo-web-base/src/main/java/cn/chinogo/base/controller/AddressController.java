package cn.chinogo.base.controller;

import cn.chinogo.base.service.AddressService;
import cn.chinogo.constant.Const;
import cn.chinogo.pojo.TbUser;
import cn.chinogo.pojo.TbUserAddr;
import cn.chinogo.redis.service.JedisClient;
import cn.chinogo.utils.FastJsonConvert;
import com.alibaba.dubbo.config.annotation.Reference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地址管理controller
 */
@Api("地址管理controller")
@RestController
@RequestMapping("/address")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    
    @Reference(version = Const.CHINOGO_ADDRESS_VERSION,retries = 0, timeout = 100000)
    private AddressService addressService;

    @Reference(version = Const.CHINOGO_REDIS_VERSION, timeout = 1000000)
    private JedisClient jedisClient;

    @Value("${redisKey.prefix.user_session}")
    private String USER_SESSION;

    @ApiOperation("通过userId获得该用户的地址列表")
    @GetMapping(value = "/list")
    public Object getAddressList(@CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        Map<String, Object> map = new HashMap<>();
        
        if (user == null) {
            map.put("status", "-2");
            map.put("msg", "failed");
            return map;
        }
        
        List<TbUserAddr> addressList = addressService.getAddressList(user.getId());

        map.put("msg", "suc");
        map.put("status", "0");
        map.put("result", addressList);

        return map;
    }

    @ApiOperation("通过地址id获得该用户的地址列表")
    @PostMapping(value = "/one/{addrId}")
    public Object getAddressListByAddrId(@PathVariable("addrId") String addrId) {
        Map<String, Object> map = new HashMap<>(3);

        TbUserAddr address = addressService.getAddressListByAddrId(addrId);

        map.put("msg", "suc");
        map.put("status", "0");
        map.put("result", address);

        return map;
    }

    @ApiOperation("添加用户地址")
    @PostMapping(value = "/add")
    public Object addAddress(@RequestBody TbUserAddr addr, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", "");
        
        if (user == null) {
            map.put("status", "-2");
            map.put("msg", "failed");
            return map;
        }
        
        addr.setUserId(user.getId().toString());
        int i = addressService.addAddress(addr);
        
        if (i == 1) {
            map.put("status", "0");
            map.put("result", "suc");
        } else {
            map.put("status", "-1");
            map.put("result", "failed");
        }

        return map;
    }

    @ApiOperation("通过地址id删除用户地址")
    @PostMapping(value = "/delete/{id}")
    public Object deleteAddress(@PathVariable("id") String id) {
        int i = addressService.deleteAddress(id);

        Map<String, Object> map = new HashMap<>();
        map.put("result", "");
        if (i == 1) {
            map.put("status", "0");
            map.put("msg", "suc");
        } else {
            map.put("status", "-1");
            map.put("msg", "failed");
        }

        return map;
    }

    @ApiOperation("更新用户地址")
    @PostMapping(value = "/update")
    public Object updateAddress(@RequestBody TbUserAddr addr, @CookieValue(Const.TOKEN_LOGIN) String token) {
        TbUser user = getUser(token);

        Map<String, Object> map = new HashMap<>();
        map.put("result", "");
        
        if (user == null) {
            map.put("status", "-2");
            map.put("msg", "failed");
            return map;
        }

        int i = addressService.updateAddress(addr, user.getId());

        if (i == 1) {
            map.put("status", "0");
            map.put("msg", "suc1");
        } else {
            map.put("status", "-1");
            map.put("msg", "failed");
        }

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
