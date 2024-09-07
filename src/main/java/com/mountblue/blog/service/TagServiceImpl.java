package com.mountblue.blog.service;

import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements  TagService{

    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findTagsByIds(List<Integer> tagIds) {
        return tagRepository.findAllById(tagIds);
    }

    @Override
    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }
}
