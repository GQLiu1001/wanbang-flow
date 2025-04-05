package com.wanbang.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.driver.common.DriverInfo;
import com.wanbang.driver.mapper.DriverInfoMapper;
import com.wanbang.driver.resp.LoginInfoResp;
import com.wanbang.driver.service.DriverInfoService;
import com.wanbang.driver.util.UserContextHolder;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
* @author 11965
* @description 针对表【driver_info(司机信息表)】的数据库操作Service实现
* @createDate 2025-04-02 20:51:11
*/
@Service
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo>
    implements DriverInfoService{
    @Resource
    private DriverInfoMapper driverInfoMapper;
    @Resource
    private WxMaService wxMaService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate redisTemplate;
    public static final String TOKEN = "delivery:login:";
    public static final String LOCATION = "driver:locations";

    @Override
    public LoginInfoResp login(String code, String phone) throws WxErrorException {
        String openid;
        //1.获取code值 使用微信工具包对象(WxMaService) 获取微信唯一标识 openid
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        openid = sessionInfo.getOpenid();
        //2.根据openid判断是否第一次登录 是-》添加信息到用户表 返回用户id值 plus登录日志
        QueryWrapper<DriverInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        DriverInfo driverInfo = driverInfoMapper.selectOne(queryWrapper);
        if (driverInfo == null) {
            //new driverInfo() 是必需的 - 因为 driverInfo 为 null 时我们需要一个新对象来存储数据。
            // 否则无法调用 setWxOpenId() 等方法，会报 NullPointerException。
            driverInfo = new DriverInfo();
            driverInfo.setOpenid(openid);
            driverInfo.setPhone(phone);
            driverInfo.setAvatar("");
            driverInfo.setName("newDriver" + phone);
            driverInfo.setAuditStatus(0);
            driverInfo.setWorkStatus(3);
            driverInfo.setMoney(BigDecimal.valueOf(0));
            driverInfo.setCreateTime(new Date());
            driverInfo.setUpdateTime(new Date());
            driverInfoMapper.insert(driverInfo);
            //MyBatis-Plus 的 insert 方法在插入成功后会自动将生成的主键（如 id）填充到实体对象中
        }
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(TOKEN + token, String.valueOf(driverInfo.getId()),30, TimeUnit.HOURS);
        LoginInfoResp loginInfoResp = new LoginInfoResp();
        loginInfoResp.setToken(token);
        BeanUtils.copyProperties(driverInfo, loginInfoResp);
        return loginInfoResp;
    }

    @Override
    public void changeInfo(String driverName, String userId) {
        LambdaUpdateWrapper<DriverInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DriverInfo::getId, Long.valueOf(userId))
                .set(DriverInfo::getName, driverName);
        driverInfoMapper.update(null, updateWrapper);
    }

    @Override
    public void logout(long id) {
        String userToken = UserContextHolder.getUserToken();
        stringRedisTemplate.delete(TOKEN + userToken);
    }

    @Override
    public void updateLocation(Integer id, BigDecimal latitude, BigDecimal longitude) {
        if (id == null || latitude == null || longitude == null) {
            throw new IllegalArgumentException("id, latitude 或 longitude 不能为空");
        }
        // 将 BigDecimal 转换为 double，Redis GEO 需要 double 类型
        double lat = latitude.doubleValue();
        double lon = longitude.doubleValue();
        // 校验经纬度范围
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("经纬度超出有效范围（纬度: -90~90，经度: -180~180）");
        }
        // 使用 Redis GEO 存储位置
        redisTemplate.opsForGeo().add(LOCATION, new Point(lon, lat), String.valueOf(id));
    }

    @Override
    public void updateDriverStatus(Long id, Integer workStatus) {
        LambdaUpdateWrapper<DriverInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DriverInfo::getId, id).set(DriverInfo::getWorkStatus, workStatus);
        driverInfoMapper.update(null, updateWrapper);
    }
}




