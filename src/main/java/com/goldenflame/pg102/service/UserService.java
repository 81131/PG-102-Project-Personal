package com.goldenflame.pg102.service;

import com.goldenflame.pg102.model.Role;
import com.goldenflame.pg102.model.User;
import com.goldenflame.pg102.repository.RoleRepository;
import com.goldenflame.pg102.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public void createUser(String username, String email, String password, String firstName, String lastName,
                           String address1, String address2, String city, String phone1, String phone2, Integer roleId) {

        String sql = "INSERT INTO users (username, email, password, first_name, last_name, address_line1, address_line2, city, primary_phone_no, secondary_phone_no, role_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, username, email, password, firstName, lastName, address1, address2, city, phone1, phone2, roleId);
    }

    public void deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }


    public void registerCustomer(String username, String email, String password, String firstName, String lastName,
                                 String address1, String address2, String city, String phone1, String phone2) {
        // First, get the ID for the CUSTOMER role
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        String sql = "INSERT INTO users (username, email, password, first_name, last_name, address_line1, address_line2, city, primary_phone_no, secondary_phone_no, role_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, username, email, password, firstName, lastName, address1, address2, city, phone1, phone2, customerRole.getId());
    }
}