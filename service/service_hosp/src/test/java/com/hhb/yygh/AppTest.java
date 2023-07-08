
package com.hhb.yygh;

import com.hhb.yygh.hosp.bean.Actor;
import com.hhb.yygh.hosp.HospApplication;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = HospApplication.class)
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testInsert(){
        mongoTemplate.insert(new Actor("1002","梁启超",true,new Date()));
    }
    //测试save，必须先查出来其他名字，再进行
    @Test
    public void testSave(){
        Actor actor = mongoTemplate.findById("1002", Actor.class);
        actor.setActorName("张小龙");
        mongoTemplate.save(actor);
    }
    @Test
    public void testDelete(){
        Query query = new Query(Criteria.where("actorName").is("张小龙"));
        DeleteResult deleteResult = mongoTemplate.remove(query,Actor.class);
        long deletedCount = deleteResult.getDeletedCount();
    System.out.println("deletedCount = " + deletedCount);
    }
    @Test
    public void testBatch(){
        List<Actor> list = new ArrayList<Actor>();
        list.add(new Actor("14","111",false,new Date()));
        list.add(new Actor("15","222",false,new Date()));
        list.add(new Actor("16","333",false,new Date()));
        list.add(new Actor("17","444",false,new Date()));
        mongoTemplate.insert(list,Actor.class);
    }
    //测试修改
    @Test
    public void testUpdate(){
        Query query = new Query(Criteria.where("actorName").is("123"));
        Update update = new Update();
        update.set("gender",true);
        update.set("birth",new Date());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Actor.class);
        System.out.println("result = " + result);
    }
    @Test
    public void testQuery(){
        //Query query = new Query(Criteria.where("gender").is(false));
        Query query = new Query(Criteria.where("actorName").regex(".*1.*"));
        List<Actor> actors = mongoTemplate.find(query, Actor.class);
        actors.stream().forEach(actor -> System.out.println("actor = " + actor));
    }
    @Test
    public void testPageQuery(){
        int pageNum = 2;
        int size = 3;
        Query query = new Query(Criteria.where("gender").is(false));
        long total = mongoTemplate.count(query, Actor.class);

        List<Actor> actors = mongoTemplate.find(query.skip((pageNum - 1) * size).limit(size), Actor.class);
        actors.stream().forEach(actor -> System.out.println("actor = " + actor));
    }
}
