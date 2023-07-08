package com.hhb.yygh.hosp.repository;

import com.hhb.yygh.hosp.bean.Actor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends MongoRepository<Actor,String> {
    public List<Actor> findByActorNameLike(String name);
}
