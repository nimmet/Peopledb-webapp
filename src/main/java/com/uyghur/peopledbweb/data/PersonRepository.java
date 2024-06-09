package com.uyghur.peopledbweb.data;

import com.uyghur.peopledbweb.biz.model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.Set;

@Repository
public interface PersonRepository extends CrudRepository<Person,Long>, PagingAndSortingRepository<Person,Long> {


    @Query(nativeQuery = true, value = "select photo_file_name from person where id in :ids")
    public Set<String> findFileNamesByIds(@Param("ids") Iterable<Long> ids);
}
