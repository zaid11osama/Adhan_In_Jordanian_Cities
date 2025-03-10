package com.arabbank.hdf.uam.brain.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {
    Collection<T> findAll();

    Collection<T> findAll(Sort sort);

    Optional<T> findById(ID id);

    long count();
}
