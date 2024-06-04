package com.uyghur.peopledbweb.data;

import com.uyghur.peopledbweb.biz.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {

}
