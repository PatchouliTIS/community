package koumakan.javaweb.community.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import koumakan.javaweb.community.annotation.LoginAuthority;
import koumakan.javaweb.community.dao.UserMapper;
import koumakan.javaweb.community.entity.User;
import koumakan.javaweb.community.service.FollowService;
import koumakan.javaweb.community.service.LikeService;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityConstant;
import koumakan.javaweb.community.util.HostHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.SocketTimeoutException;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/24 16:52
 * @Decription:
 */

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

     private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

     @Value("${community.path.upload}")
     private String uploadPath;

     @Value("${community.path.domain}")
     private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;


    @Autowired
    private HostHandler hostHandler;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @LoginAuthority
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }


    /**
     * 将网页上传的头像文件保存到本地服务器中
     * @param headerImage MultipartFile是由SpringMVC自动从前端表单页面中捕获的
     * @param model
     * @return
     */
    @LoginAuthority
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if(headerImage == null) {
            model.addAttribute("error", "您还未选择照片！");
            return "/site/setting";
        }


        String filename = headerImage.getOriginalFilename();

        // 判断图片格式是否正确
        String suffix = filename.substring(filename.lastIndexOf('.'));
        if(StringUtils.isBlank(suffix) == true) {
            model.addAttribute("error", "图片格式错误！");
            return "/site/setting";
        }

        // TODO:在picPath下新建文件并将headerImage写入该文件内。
        File tmp = new File(uploadPath + "/" + filename);
        try {
            // 写入服务器本地文件中保存
            headerImage.transferTo(tmp);
        } catch (IOException e) {
            LOGGER.error("保存头像失败！服务器发生错误：" + e.getMessage());
            throw new RuntimeException("保存头像失败！服务器发生错误：\n", e);
        }


        // TODO:获取当前用户并且更新用户的headerUrl
        User user = hostHandler.getUser();
        // 使用web路径而非本地路径
        String webPath = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), webPath);

        return "redirect:/index";
    }


    /**
     * 将网页端发出的头像请求转换为对本地头像文件的读取。
     * 该方法是在 index.html 中 头像 <th:src=${loginUser.headerUrl}> 中自动调用 的
     * @param fileName  web文件路径
     * @param response
     * @return 为什么没有返回值？使用response直接设置头像
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.'));

        // 不需要判断，因为进行到这一步时，filename肯定是正常的
        /*if(StringUtils.isBlank(suffix)) {
            LOGGER.error("文件格式错误！：");
        }*/

        // 服务器存放路径
        String serverPath = uploadPath + "/" + fileName;


        // 响应图片
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fis = new FileInputStream(serverPath);
                OutputStream os = response.getOutputStream();
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }

        } catch (IOException e) {
            LOGGER.error("读取头像失败: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 个人主页显示
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);

        User currentLoginUser = hostHandler.getUser();

        if(user == null) {
            throw new RuntimeException("查询个人资料失败！该用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);


        // 点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 实体关注者的数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        System.out.println("》》》》》followeeCount:" + followeeCount);

        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        System.out.println("》》》》》followerCount:" + followerCount);

        // 是否已关注
        boolean isFollowed;
        if(currentLoginUser == null) {
            isFollowed = false;
        } else {
            isFollowed = followService.isFollowed(currentLoginUser.getId(), ENTITY_TYPE_USER, userId);
        }
        System.out.println("》》》》》isFollowed:" + isFollowed);

        model.addAttribute("hasFollowed", isFollowed);

        return "/site/profile";
    }
}
