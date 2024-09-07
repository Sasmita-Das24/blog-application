package com.mountblue.blog.service;

import com.mountblue.blog.entity.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagService {
    List<Tag> findTagsByIds(List<Integer> tagIds);

    List<Tag> findAllTags();
}
