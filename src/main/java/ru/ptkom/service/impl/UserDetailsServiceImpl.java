package ru.ptkom.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ptkom.configuration.WebSecurityConfiguration;
import ru.ptkom.dao.RoleDAO;
import ru.ptkom.dao.UserDAO;
import ru.ptkom.model.Role;
import ru.ptkom.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final RoleDAO roleDAO;
    private final UserDAO userDAO;

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder, RoleDAO roleDAO, UserDAO userDAO) {
        this.passwordEncoder = passwordEncoder;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
    }

    public User findByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    public void updateUser(User newUserData) {
        User user = userDAO.getUserById(newUserData.getId());
        processUsernameUpdate(user, newUserData);
        processPasswordUpdate(user, newUserData);
        processRoleUpdate(user, newUserData);
        //log.debug("User " + user.getUsername() + " send to update method.");
        userDAO.updateUser(user);
    }

    private void processRoleUpdate(User currentUserData, User newUserData) {
        Collection<Role> roles = newUserData.getRoles();
        if (roles.size() != 0) {
            currentUserData.setRoles(roles);
            repairRoleList(currentUserData);
        }
    }

    private void processUsernameUpdate(User currentUserData, User newUserData) {
        String newUsername = newUserData.getUsername();
        if (newUsername != "" && newUsername != currentUserData.getUsername()) {
            currentUserData.setUsername(newUsername);
        }
    }

    private void processPasswordUpdate(User currentUserData, User newUserData) {
        String newPassword = newUserData.getPassword();
        if (newPassword != "" || newPassword != null) {
            currentUserData.setPassword(newPassword);
            encodePassword(currentUserData);
        }
    }

    private void repairRoleList(User user) {
        user.setRoles(
                user.getRoles().stream().parallel()
                        .map(Role::getName)
                        .map(roleDAO::getRoleByName)
                        .toList()
        );
    }

    private void encodePassword(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        //log.debug("Password to " + user.getUsername() + " encoded.");
    }


    private void saveUser(User user) {
        //log.debug("User " + user.getUsername() + " send to add method.");
        userDAO.saveUser(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to find username: " + username);
        User user = findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

    }
}
