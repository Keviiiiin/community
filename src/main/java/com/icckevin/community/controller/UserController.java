package com.icckevin.community.controller;

import com.icckevin.community.annotation.LoginRequired;
import com.icckevin.community.entity.User;
import com.icckevin.community.service.FollowService;
import com.icckevin.community.service.LikeService;
import com.icckevin.community.service.UserService;
import com.icckevin.community.utils.CommunityUtil;
import com.icckevin.community.utils.EntityTypeConstant;
import com.icckevin.community.utils.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @description:
 * @author: iccKevin
 * @create: 2020-05-21 20:11
 **/
@Controller
@RequestMapping("/user")
public class UserController implements EntityTypeConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage,Model model){
        if(headerImage == null){
            model.addAttribute("error","请选择一张图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        // 后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;

        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片格式
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(value = "/password",method = RequestMethod.POST)
    public String uploadPassword(Model model, String oldPassword, String newPassword, String confirmPassword,
                                 @CookieValue("ticket") String ticket){
//  输入框为空的判断交给前端
        //        if(StringUtils.isBlank(oldPassword)){
//            model.addAttribute("oldError","原密码不能为空!");
//        }
//        else if(StringUtils.isBlank(newPassword) ) {
//            model.addAttribute("newError","新密码不能为空!");
//        }
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmError","两次输入不一致！");
            return "/site/setting";
        }
        else {
            User user = hostHolder.getUser();
            String password = user.getPassword();
            if (!password.equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
                model.addAttribute("oldError", "原密码不正确!");
                return "/site/setting";
            }
            else if(oldPassword.equals(newPassword)){
                model.addAttribute("newError","新密码不能和原密码相同!");
                return "/site/setting";
            }
            else {
                userService.updatePassword(user.getId(),CommunityUtil.md5(newPassword + user.getSalt()));
                userService.logout(ticket);
                model.addAttribute("msg","密码修改成功，请重新登录!");
                model.addAttribute("target","/login");
            }
        }
        return "/site/operate-result";
    }

    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable int userId, Model model){
        User user = userService.selectById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);
        long likeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("userLikeCount",likeCount);

        long followeeCount = followService.getFolloweeCount(userId, ENTITY_TYPE_USER);
        long followerCount = followService.getFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followeeCount",followeeCount);
        model.addAttribute("followerCount",followerCount);

        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}