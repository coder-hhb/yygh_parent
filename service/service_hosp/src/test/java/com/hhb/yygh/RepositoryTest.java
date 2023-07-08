package com.hhb.yygh;

import com.hhb.yygh.hosp.HospApplication;
import com.hhb.yygh.hosp.bean.Actor;
import com.hhb.yygh.hosp.repository.ActorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = HospApplication.class)
@RunWith(SpringRunner.class)
public class RepositoryTest {
    @Autowired
    private ActorRepository actorRepository;

    @Test
    public void testInsert(){
        actorRepository.save(new Actor("19","jiadoja",false,new Date()));
    }
    @Test
    public void testDelete(){
        Actor actor = new Actor();
        //删除要设置id
        actor.setId("11");
        actorRepository.delete(actor);
    }
    @Test
    public void testQuery(){
        Actor actor = actorRepository.findById("12").get();
    System.out.println("actor = " + actor);
    }

    @Test
    public void testQuery2(){
        Actor actor = new Actor();
        actor.setGender(false);
        Example<Actor> example = Example.of(actor);
        List<Actor> alls = actorRepository.findAll(example);
       for (Actor all : alls) {
      System.out.println("all = " + all);
       }
    }
    @Test
    public void testPage(){
        int pageNum = 1;
        int pageSize = 3;
        Actor actor = new Actor();
        actor.setGender(false);
        Example<Actor> example = Example.of(actor);
        Pageable pageable = PageRequest.of(pageNum,pageSize, Sort.by("id").descending());
        Page<Actor> page = actorRepository.findAll(example, pageable);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        List<Actor> actorList = page.getContent();
        for (Actor actor1 : actorList) {
             System.out.println("actor1 = " + actor1);
        }
    }
    @Test
    public void test(){
        List<Actor> list = actorRepository.findByActorNameLike("22");
        for (Actor actor : list) {
            System.out.println("actor = " + actor);
        }
    }
}
