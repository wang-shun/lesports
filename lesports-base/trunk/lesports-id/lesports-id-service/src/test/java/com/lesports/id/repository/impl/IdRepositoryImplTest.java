package com.lesports.id.repository.impl;

import com.lesports.AbstractIntegrationTest;
import com.lesports.id.api.IdType;
import com.lesports.id.repository.IdRepository;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

public class IdRepositoryImplTest extends AbstractIntegrationTest {

    @Resource
    private IdRepository idRepository;

    @Test
    public void nextId() {
        long value = idRepository.nextId(IdType.ALBUM);
        Assert.assertEquals(1, value);
    }

}