package koumakan.javaweb.community.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import koumakan.javaweb.community.service.UserService;
import koumakan.javaweb.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Package: koumakan.javaweb.community.controller
 * @Author: Alice Maetra
 * @Date: 2023/3/13 20:28
 * @Decription:
 */

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    @Qualifier("userService")
    private UserService _userService;

    @RequestMapping("/helloworld")
    @ResponseBody       // 用来将String类型字符转换为网页可识别的内容
    public String sayHello() {
        return "HelloWorld";
    }

    @RequestMapping("/service")
    @ResponseBody
    public String getUserService() {
        return this._userService.useDao();
    }

    @RequestMapping("/http")
    public  void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求数据


        // 返回响应数据
        response.setContentType("text/html;charset=uft-8");
        try (PrintWriter writer = response.getWriter()){
            writer.write("<h1>我测</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  获取 GET 报文
     *  .../students?current=1&classes=114514
     */
    @RequestMapping("students")
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "19") int current,
            @RequestParam(name = "classes", required = false, defaultValue = "114515") int classes
    )  {
        System.out.println(current + " " + classes);
        return current + " " + classes;
    }

     // /student/123
     @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
     @ResponseBody
     public String getStudent(@PathVariable("id") int idx) {
         System.out.println(idx);
         return "current student id:" + idx;
     }

    /**
     *  POST
     *  为什么用 POST ?
     *  1. 隐藏服务器内容，更加安全。GET请求的内容直接写在URL上。
     *  2. 限制url路径长度
     *
     */
    @RequestMapping(path="/student", method = RequestMethod.POST)
    @ResponseBody
    public String postStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "POST SUCCESS";
    }


    /**
     * 返回动态网页数据： Thymeleaf的使用
     * Thymeleaf自动从Model中获取数据
     *
     * 不用加@ResponseBody，因为直接是HTML文件
     *
     * 1. ModelAndView
     */
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "田所浩二");
        mav.addObject("age", 30);
        // 视图模板是从 /resource/templates/ 文件夹中提取
        mav.setViewName("/demo/view");
        return mav;
    }


    /**
     *  2. Model
     */
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "USTC");
        model.addAttribute("age", "114514");
        return "demo/view -> school";
    }


    /**
     * 响应 -> 向浏览器传递JSON数据（异步请求）
     * POJO -> JSON字符串 -> JS Object
     * Content-type:application/json
     */

    @RequestMapping(path="/employee", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmployee() {
        Map<String, Object> employee = new LinkedHashMap<>(){{
            put("name", "田所浩二");
            put("salary", 8000f);
            put("age", 24);
        }};

        return employee;
    }

    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("UUIDCode", CommunityUtil.generateUUID());

        // 1. 设置cookie生效的路径
        // 并不是所有资源都需要保存上下文关系
        cookie.setPath("/community/alpha");

        // 2. 设置cookie生效时间，单位是秒(s)
        // 设置后cookie将存储在硬件上
        cookie.setMaxAge(60 * 10);

        response.addCookie(cookie);

        return "set cookie";
    }


    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("UUIDCode") String code) {
        return code;
    }


    /**
     * session 基于 cookie
     * 存储在服务器端，安全性更高 -> 增加了服务器的压力。
     * 可以存储任意类型的数据 Object。
     * 服务器自动创建，不像cookie需要手动创建
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession httpSession) {
        httpSession.setAttribute("user", "野兽先辈");
        httpSession.setAttribute("age", 24);
        return "SET SESSION";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession httpSession) {
        httpSession.getAttribute("user");
        httpSession.getAttribute("age");
        return "GET SESSION";
    }

}
