package com.gr.modules.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.Producer;
import com.gr.constant.Constants;
import com.gr.constant.R;
import com.gr.exception.RRException;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.ip.IPUtils;
import com.gr.manager.AsyncManager;
import com.gr.utils.CookieUtils;
import com.gr.uuid.IdUtils;
import com.gr.annotation.RepeatSubmit;
import com.gr.jwt.JwtUtil;
import com.gr.redis.RedisKey;
import com.gr.redis.RedisUtils;
import com.gr.utils.ShiroUtils;
import com.gr.async.AsyncFactory;
import com.gr.sys.entity.SysLoginUserEntity;
import com.gr.sys.entity.SysUserEntity;
import com.gr.sys.entity.SysUserPostEntity;
import com.gr.sys.service.SysUserPostService;
import com.gr.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 *
 */
@Slf4j
@Api(tags = "登录相关")
@Controller
public class SysLoginController extends AbstractController
{
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserPostService sysUserPostService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Resource(name = "captchaProducer")
	private Producer captchaProducer;
	@Resource(name = "captchaProducerMath")
	private Producer captchaProducerMath;
	@Value("${fast.captchaType}")
	private String captchaType;

	/**
	 * 获取验证码
	 * @param response
	 * @return
	 */
	@ResponseBody
	@GetMapping("/sys/captchaImage")
	@ApiOperation(value = "获取验证码")
	@RepeatSubmit
	public R captcha(HttpServletResponse response) {

		// 验证码key信息
		String uuid = IdUtils.simpleUUID();
		String verifyKey = com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY + uuid;

		String capStr = null, code = null;
		BufferedImage image = null;

		// 生成验证码
		if ("math".equals(captchaType)) {
			String capText = captchaProducerMath.createText();
			capStr = capText.substring(0, capText.lastIndexOf("@"));
			code = capText.substring(capText.lastIndexOf("@") + 1);
			image = captchaProducerMath.createImage(capStr);
		} else if ("char".equals(captchaType)) {
			capStr = code = captchaProducer.createText();
			image = captchaProducer.createImage(capStr);
		}
		//存入redis缓存
		redisUtils.setCacheObject(verifyKey, code, 2, TimeUnit.MINUTES);
		//转换流信息写出
		FastByteArrayOutputStream os = new FastByteArrayOutputStream();
		//输出到浏览器
		try {
			ImageIO.write(image, "jpg", os);
		} catch (IOException e) {
			log.error("验证码输出失败{}" + e.getMessage());
		}

		return R.ok().put("uuid", uuid).put("img", Base64.encodeBase64String(os.toByteArray()));
	}


	/**
	 * 账号登录
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/login", method = RequestMethod.POST)
	@ApiOperation(value = "账号登录", notes = "传入字段：username：账号，password：密码，captcha：验证码，uuid：唯一Id, loginType：登录类型 0.账号密码 1.微信")
	@RepeatSubmit
	public R login(SysLoginUserEntity loginUser, HttpServletResponse response, HttpServletRequest request) {

		if(loginUser.isCaptchaOnOff()){
			//验证码key
			String verifyKey = com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY + loginUser.getUuid();
			//获取验证码
			String kaptcha = redisUtils.getCacheObject(verifyKey);
			//删除验证码
			redisUtils.deleteObject(verifyKey);
			//验证码效验
			if (kaptcha == null) {
				throw new ResultException(ResultEnum.ERR009);
			}
			if (!loginUser.getCode().equalsIgnoreCase(kaptcha)) {
				throw new ResultException(ResultEnum.ERR010);
			}
		}
		//监控账号密码错误次数
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		if ("lock".equals(opsForValue.get(RedisKey.getLoginLock(loginUser.getAccount())))){
			log.debug("对用户[" + loginUser.getAccount() + "]进行登录验证...验证未通过,账号或密码错误次数大于"+Integer.parseInt(opsForValue.get(RedisKey.getLoginKey(loginUser.getAccount())))+"次,账户已锁定");
			throw new ResultException(ResultEnum.ERR011);
		}
		SysUserEntity user = null;
		String errorMsg = null;
		try {
			//用户信息
			user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getAccount, loginUser.getAccount()).or().eq(SysUserEntity::getPhone, loginUser.getAccount()));

			//账号不存在、密码错误
			if (user == null || !user.getPassword().equals(ShiroUtils.sha256(loginUser.getPassword(), user.getSalt()))) {

				if(user == null){
					throw new ResultException(ResultEnum.ERR007);
				}

				//使用redis检查账号密码输入错误次数
				opsForValue.increment(RedisKey.getLoginKey(loginUser.getAccount()), 1);
				int number = Integer.parseInt(opsForValue.get(RedisKey.getLoginKey(loginUser.getAccount())));

				//计数大于5次，设置用户被锁定10分钟
				if (number >= 5) {
					opsForValue.set(RedisKey.getLoginLock(loginUser.getAccount()), "lock");
					redisUtils.expire(RedisKey.getLoginLock(loginUser.getAccount()), 60);
					throw new ResultException(ResultEnum.ERR011);
				}

				if(number >= 3){
					throw new RRException("账号或密码不正确，还剩余"+ (5 - number) +"次机会");
				}

				throw new ResultException(ResultEnum.ERR012);
			}

			//账号锁定
			if (user.getStatus() == Constants.SUPER_ADMIN) {
				errorMsg = ResultEnum.ERR008.getMessage();
				throw new ResultException(ResultEnum.ERR008);
			}

		}catch (Exception e) {
			errorMsg = StringUtils.substring(e.getMessage(), 0, 5000);
			throw new RRException(e.getMessage());

		} finally {
			if (user != null) {
				AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getAccount(), user.getUsername(), errorMsg == null ? Constants.ZERO : Constants.SUPER_ADMIN, errorMsg == null ? "登陆成功" : errorMsg));
			}
		}
		// 生成token
		String token = JwtUtil.sign(loginUser.getAccount(), user.getPassword());
		// 设置超时时间
		redisUtils.set(Constants.PREFIX_USER_TOKEN + token, token);
		redisUtils.expire(Constants.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		// 保存在线用户状态
		redisUtils.set(Constants.PREFIX_ONLINE_USER + user.getUserId(), user.setLoginTime(new Date()).setLastAccessTime(user.getLoginTime()).setIp(IPUtils.getIpAddr()).setSid(IdUtils.fastUUID()).setToken(token).setLoginStatus(Constants.SUPER_ADMIN).setMobile(loginUser.getLoginType()));
		//redisUtils.expire(Constant.PREFIX_ONLINE_USER + user.getUserId(), JwtUtil.EXPIRE_TIME*2 / 1000);

		//写入token令牌
		request.setAttribute("Authorization", token);

		//清除用户的相关信息的cookie
		CookieUtils.newBuilder(response).httpOnly().maxAge(0).build(Constants.AUTH_TOKEN, token);

		if(opsForValue.get(RedisKey.getLoginKey(loginUser.getAccount())) != null){
			//清空登录计数
			opsForValue.set(RedisKey.getLoginKey(loginUser.getAccount()), String.valueOf(Constants.ZERO));
			//设置未锁定状态
			opsForValue.set(RedisKey.getLoginLock(loginUser.getAccount()), "unlock");
		}

		return R.ok().put("userInfo", user.setPassword(null).setSalt(null)).put(Constants.AUTH_TOKEN, token);
	}

	/**
	 * 退出
	 */
	@ApiOperation(value = "退出")
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			//用户退出逻辑
			String token = CookieUtils.getCookie(request);
			if(StringUtils.isEmpty(token)) {
				return "redirect:/login";
			}
			String account = JwtUtil.getUsername(token);
			SysUserEntity user = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getAccount, account).or().eq(SysUserEntity::getPhone, account));
			if(user != null){
				//更新用户操作时间
				SysUserEntity sysUser = redisUtils.getCacheObject(Constants.PREFIX_ONLINE_USER + user.getUserId());
				redisUtils.set(Constants.PREFIX_ONLINE_USER + user.getUserId(), sysUser.setLastAccessTime(new Date()).setLoginStatus(Constants.ZERO));

				//清空用户登录Token缓存
				redisUtils.del(Constants.PREFIX_USER_TOKEN + user.getToken());

				//清空用户登录Shiro权限缓存
				redisUtils.del(Constants.PREFIX_USER_SHIRO_CACHE + user.getUserId());

				//清除用户的相关信息的cookie
				CookieUtils.delCookie(response, request);

				//调用shiro的logout
				SecurityUtils.getSubject().logout();
				log.info("用户名:  " + user.getAccount() + ",退出成功！ ");
			}
		} catch (Exception e) {
			log.error("退出失败{}", e.getMessage());
		}
		return "redirect:/login";
	}

}
