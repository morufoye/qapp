package com.QAPP.api.repo;


import com.QAPP.api.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long>{
    Role findRoleByName(String name);
}
