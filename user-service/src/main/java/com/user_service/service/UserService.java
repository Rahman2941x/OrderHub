package com.user_service.service;

import com.user_service.dto.AuthDTO;
import com.user_service.dto.ResponseDTO;
import com.user_service.dto.UserUpdateDTO;
import com.user_service.entity.Role;
import com.user_service.entity.User;
import com.user_service.dto.UserDTO;
import com.user_service.exception.DetailMissingException;
import com.user_service.exception.UserAlreadyExistException;
import com.user_service.exception.UserNotActiveException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.management.RuntimeMBeanException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User registerUser(UserDTO userDTO) {
        Optional<User> userCheck = userRepository.findByEmail(userDTO.email());
        if (userCheck.isPresent()) {
            throw new UserAlreadyExistException(userDTO.email());
        }

        User user = new User(
                userDTO.username(),
                passwordEncoder.encode(userDTO.password()),
                userDTO.email(),
                userDTO.mobileNumber(),
                userDTO.address(),
                userDTO.role()
        );

        userRepository.save(user);
        user.setPassword(userDTO.password());

        return user;
    }

    public UserUpdateDTO updateUser(Long id,UserUpdateDTO updateDTO) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(!user.getActive()){
            throw new UserNotActiveException(user.getEmail());
        }

        if(updateDTO!=null){Optional.ofNullable(updateDTO.username())
                .ifPresent(user::setUsername);
        Optional.ofNullable(updateDTO.email())
                .ifPresent(user::setEmail);
        Optional.ofNullable(updateDTO.mobileNumber())
                .ifPresent(user::setMobileNumber);
        Optional.ofNullable(updateDTO.address())
                .ifPresent(user::setAddress);
        }else {throw new RuntimeException("Details is null");}

        userRepository.save(user);

        return updateDTO;
    }

    public String  deleteUser(Long id, String email) {
        if (id == null && email == null) {
            throw new DetailMissingException();
        }

        User user;
        if (id != null) {
            user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        } else {
            user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        }
        userRepository.delete(user);


      return "User " + user.getEmail() + " has been deleted";
    }

    public String changeActivationStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));

        if(!user.getEmail().equals(auth.getName()) && !isAdmin){
            throw new RuntimeException("Respective user/Admin can only activate/De Activate the account");
        }

        if (user.getActive().equals(isActive)) {
            return isActive ? "User Already Active" : "User already De-activated";
        }

        user.setActive(isActive);
        userRepository.save(user);

       return  isActive ? "User has been Active" : "User has been  De-activated";
    }


    public Page<UserUpdateDTO> getAllUser(int size, int page, Boolean isSorted) {

        Pageable pageable;

        if(isSorted){
            pageable=PageRequest.of(page,size,Sort.by("id"));
        }else pageable= PageRequest.of(page,size);

        Page<User> users=userRepository.findAll(pageable);

        Page<UserUpdateDTO> pageDto=users.map(
                user -> new UserUpdateDTO(
                        user.getUsername(),
                        user.getEmail(),
                        user.getMobileNumber(),
                        user.getAddress(),
                        user.getActive()
                )
        );

        if (users.isEmpty()){
            throw new DetailMissingException();
        }

        return pageDto;
    }

    @Transactional
    public String changePassword(AuthDTO authDTO,String newPassword) {
        if (authDTO == null) {
            throw new DetailMissingException();
        }
        User user = userRepository.findByEmail(authDTO.email()).orElseThrow(UserNotFoundException::new);

        if (!user.getActive()) {
            throw new UserNotActiveException(user.getEmail());
        }

        user.setPassword(
                passwordEncoder
                        .encode(
                                newPassword
                        )
        );

        return "Password changed successfully";
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return new  UserDTO(
                user.getUsername(),
                null,
                user.getEmail(),
                user.getMobileNumber(),
                user.getAddress(),
                user.getRole(),
                user.getActive()
        );
    }


    public UserDTO getUserByIdOrEmail(Long id, String email) {
        if(id==null && email==null){
            throw new DetailMissingException();
        }

        User user;

        if(id!=null){
            user=userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        }else {
            user=userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        }

        return new UserDTO(
                user.getUsername(),
                null,
                user.getEmail(),
                user.getMobileNumber(),
                user.getAddress(),
                user.getRole(),
                user.getActive()
        );
    }

    public List<UserDTO> getByRole(Role role) {
        List<User> userList=userRepository.findByRole(role).orElseThrow(DetailMissingException::new);

        return userList.stream().map(
                user->new UserDTO(
                        user.getUsername(),
                        null,
                        user.getEmail(),
                        user.getMobileNumber(),
                        user.getAddress(),
                        user.getRole(),
                        user.getActive()
                )
        ).toList();
    }

    public String changeRoleAsAdmin(Long id) {
        User user=userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if(Boolean.FALSE.equals(user.getActive())){
            throw  new UserNotActiveException(user.getEmail());
        }

        if (Role.ADMIN.equals(user.getRole())){
            return "User "+user.getEmail()+" already being as Admin";
        }
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        return "User "+user.getEmail()+" has been updated as Admin";
    }
}
