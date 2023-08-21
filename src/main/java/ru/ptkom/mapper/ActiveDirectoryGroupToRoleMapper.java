package ru.ptkom.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;
import ru.ptkom.model.enums.Authority;
import ru.ptkom.service.ConfigurationFIleService;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@Component
public class ActiveDirectoryGroupToRoleMapper implements GrantedAuthoritiesMapper {

    private static final Logger log = LoggerFactory.getLogger(ActiveDirectoryGroupToRoleMapper.class);

    private String administratorRoleName;

    private String userRoleName;

    private final ConfigurationFIleService configurationFIleService;


    public ActiveDirectoryGroupToRoleMapper(ConfigurationFIleService configurationFIleService) {
        this.configurationFIleService = configurationFIleService;
        initializeConfigurationProperties();
    }

    private void initializeConfigurationProperties() {
        administratorRoleName = configurationFIleService.getActiveDirectoryAdministratorRoleName();
        userRoleName = configurationFIleService.getActiveDirectoryUserRoleName();
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<Authority> roles = EnumSet.noneOf(Authority.class);

        authorities.forEach(authority -> {
            if (authority.getAuthority().equals(administratorRoleName)) {
                roles.add(Authority.ROLE_ADMIN);
            } else if (authority.getAuthority().equals(userRoleName)) {
                roles.add(Authority.ROLE_USER);
            }
        });
        roles.forEach(role -> log.info(role.getAuthority()));
        return roles;
    }
}
