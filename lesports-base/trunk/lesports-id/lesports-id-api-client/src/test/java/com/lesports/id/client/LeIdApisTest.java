package com.lesports.id.client;

import com.lesports.id.api.IdType;
import junit.framework.TestCase;
import org.junit.Test;

public class LeIdApisTest extends TestCase {

    @Test
    public void testNextId() throws Exception {
        for (int i = 0;i<1000;i++) {
            long id = LeIdApis.nextId(IdType.CAROUSEL);
            System.out.println("==================================");
            System.out.println(id);
            System.out.println("==================================");
        }

//        Assert.assertEquals(1004, id);
    }

	@Test
	public void testGetMatchIdByEpisodeId() throws Exception {
		long id = LeIdApis.getMatchIdByEpisodeId(114667005L);
		System.out.println("==================================");
		System.out.println(id);
		System.out.println("==================================");
//        Assert.assertEquals(1004, id);
	}
}