package cn.chinogo.address.service;

import cn.chinogo.pojo.TbUserAddr;

import java.util.List;

/**
 * @author chinotan
 */
public interface AddressService {

    /**
     * 通过userId获得该用户的地址列表
     *
     * @param userId
     * @return
     */
    List<TbUserAddr> getAddressList(Long userId);

    /**
     * 通过地址id获得地址
     * 
     * @param addrId
     * @return
     */
    TbUserAddr getAddressListByAddrId(String addrId);
    
    /**
     * 添加用户地址
     * 
     * @param addr
     * @return
     */
    int addAddress(TbUserAddr addr);

    /**
     * 通过地址id删除用户地址
     * 
     * @param id
     * @return
     */
    int deleteAddress(String id);

    /**
     * 更新用户地址
     * 
     * @param addr
     * @return
     */
    int updateAddress(TbUserAddr addr, Long userId);
    
}
