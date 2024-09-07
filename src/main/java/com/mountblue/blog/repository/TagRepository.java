package com.mountblue.blog.repository;

import com.mountblue.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {

    Tag findByName(String name);
}
