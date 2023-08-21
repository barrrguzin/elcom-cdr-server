package ru.ptkom.dao;

import org.springframework.stereotype.Component;
import ru.ptkom.model.Role;
import ru.ptkom.repository.RoleRepository;

import java.util.List;

@Component
public class RoleDAO {

    private final RoleRepository roleRepository;

    public RoleDAO(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getListOfRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        try {
            return roleRepository.findByName(name).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("Unable to find roles with name: " + name);
        }
    }

}
