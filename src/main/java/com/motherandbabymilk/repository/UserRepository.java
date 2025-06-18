package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Roles;
import com.motherandbabymilk.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findUsersByUsername(String username);
    Users findUsersById(int id);
    public List<Users> findByRoles(Roles roles);
    List<Users> findUsersByStatusTrue();

    @Query("select count(u) from Users u where u.roles = :roles")
    long countByRole(@Param("roles") Roles role);
}
