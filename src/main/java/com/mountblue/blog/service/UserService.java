package com.mountblue.blog.service;

import com.mountblue.blog.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    boolean register(User user);
}
