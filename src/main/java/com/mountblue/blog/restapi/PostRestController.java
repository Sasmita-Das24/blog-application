package com.mountblue.blog.restapi;

import com.mountblue.blog.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostRestController {

    @GetMapping("/hello")
    public User sayHello(){
        User user = new User();
        user.setName("PRADEEP");
        return user;//"Hello World";
    }
}
