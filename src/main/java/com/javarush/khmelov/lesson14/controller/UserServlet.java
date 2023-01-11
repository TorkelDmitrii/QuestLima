package com.javarush.khmelov.lesson14.controller;

import com.javarush.khmelov.lesson14.entity.Role;
import com.javarush.khmelov.lesson14.entity.User;
import com.javarush.khmelov.lesson14.service.ImageService;
import com.javarush.khmelov.lesson14.service.UserService;
import com.javarush.khmelov.lesson14.util.Go;
import com.javarush.khmelov.lesson14.util.Jsp;
import com.javarush.khmelov.lesson14.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("serial")
@WebServlet(name = "UserServlet", value = Go.USER)
@MultipartConfig(fileSizeThreshold = 1 << 20)
public class UserServlet extends HttpServlet {

    UserService userService = UserService.USER_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String parameterId = request.getParameter(Key.ID);
        HttpSession session = request.getSession();
        request.setAttribute(Key.ID, parameterId);
        if (Objects.nonNull(parameterId)) {
            long id = Long.parseLong(parameterId);
            Optional<User> optionalUser = userService.get(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                request.setAttribute(Key.USER, user);
            }
            Jsp.forward(request, response, Key.USER);
        }
        response.sendRedirect(Key.USERS);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = User.builder()
                .id(Long.valueOf(request.getParameter(Key.ID)))
                .login(request.getParameter(Key.LOGIN))
                .password(request.getParameter(Key.PASSWORD))
                .role(Role.valueOf(request.getParameter(Key.ROLE)))
                .build();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey("create")) {
            userService.create(user);
        } else if (parameterMap.containsKey("update")) {
            userService.update(user);
        } else if (parameterMap.containsKey("delete")) {
            userService.delete(user);
        } else throw new IllegalStateException("unknown command");
        imageService.uploadImage(request, user.getId());
        response.sendRedirect(Key.USERS);
    }
}