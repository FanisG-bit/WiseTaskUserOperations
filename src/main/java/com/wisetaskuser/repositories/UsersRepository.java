package com.wisetaskuser.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.wisetaskuser.entities.User;

/**
 * @author Theofanis Gkoufas
 *
 */
@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {

}
