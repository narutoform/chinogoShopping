package cn.chinogo.base.service.impl;

import cn.chinogo.base.service.AddressService;
import cn.chinogo.constant.Const;
import cn.chinogo.mapper.TbUserAddrMapper;
import cn.chinogo.pojo.TbUserAddr;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author chinotan
 */
@Service(version = Const.CHINOGO_ADDRESS_VERSION, timeout = 1000000, retries = 0)
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements AddressService {

    @Autowired
    TbUserAddrMapper addrMapper;

    /**
     * 通过userId获得该用户的地址列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<TbUserAddr> getAddressList(Long userId) {
        Wrapper<TbUserAddr> wrapper = new EntityWrapper<>();
        wrapper.where("user_id={0}", userId)
            .orderBy("created", true);

        List<TbUserAddr> list = addrMapper.selectList(wrapper);

        return list;
    }

    /**
     * 通过地址id获得地址
     *
     * @param addrId
     * @return
     */
    @Override
    public TbUserAddr getAddressListByAddrId(String addrId) {
        TbUserAddr userAddr = addrMapper.selectById(addrId);

        return userAddr;
    }

    /**
     * 添加用户地址
     *
     * @param addr
     * @return
     */
    @Override
    public int addAddress(TbUserAddr addr) {
        Date date = new Date();
        addr.setCreated(date);
        addr.setUpdated(date);

        setDefaultAddress(addr, Long.parseLong(addr.getUserId()), date);
        
        Integer insert = addrMapper.insert(addr);

        return insert;
    }

    /**
     * 通过地址id删除用户地址
     *
     * @param id
     * @return
     */
    @Override
    public int deleteAddress(String id) {
        Integer integer = addrMapper.deleteById(id);

        return integer;
    }

    /**
     * 更新用户地址
     *
     * @param addr
     * @return
     */
    @Override
    public int updateAddress(TbUserAddr addr, Long userId) {
        Date date = new Date();
        addr.setUpdated(date);

        setDefaultAddress(addr, userId, date);
        
        Integer integer = addrMapper.updateById(addr);
        
        return integer;
    }
    
    private void setDefaultAddress(TbUserAddr addr, Long userId, Date date) {
        if (addr.getIsDefault() == 1) {
            List<TbUserAddr> addressList = this.getAddressList(userId);
            for (TbUserAddr userAddr: addressList) {
                if (userAddr.getIsDefault() == 1) {
                    userAddr.setIsDefault(0);
                    userAddr.setUpdated(date);
                    addrMapper.updateById(userAddr);
                }
            }
        }
    }
}
