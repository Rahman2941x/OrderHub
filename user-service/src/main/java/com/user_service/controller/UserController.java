package com.user_service.controller;

import com.user_service.dto.AuthDTO;
import com.user_service.dto.ResponseDTO;
import com.user_service.dto.UserDTO;
import com.user_service.dto.UserUpdateDTO;
import com.user_service.entity.Role;
import com.user_service.exception.DetailMissingException;
import com.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/message")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMessage() {
        return ResponseEntity.ok("Success message");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/all")
    public ResponseEntity<ResponseDTO<Page<UserUpdateDTO>>> getAllUser(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "false") Boolean isSorted) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.getAllUser(size, page, isSorted)));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<?>> registerUser(
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.registerUser(userDTO)));
    }

    @PatchMapping("/update/id/{id}")
    public ResponseEntity<ResponseDTO<?>> updateUser(@PathVariable long id,
                                                     @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.updateUser(id, userUpdateDTO)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO<?>> deleteUserByOrEmail(@RequestParam(required = false) Long id,
                                                              @RequestParam(required = false) String email) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.deleteUser(id, email)));
    }

    @PatchMapping("/active")
    public ResponseEntity<ResponseDTO<?>> changeActivationStatus(@RequestParam(required = true) Long id,
                                                                 @RequestParam(required = true) Boolean isActive
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.changeActivationStatus(id, isActive)));
    }

    @PatchMapping("/change/password")
    public ResponseEntity<ResponseDTO<?>> changePassWord(@RequestBody AuthDTO authDTO,
                                                         @RequestParam(required = true) String newPassword) {
        return ResponseEntity.ok(new ResponseDTO<>(200, userService.changePassword(authDTO,newPassword)));
    }

    @GetMapping("/get/id/unique")
    public ResponseEntity<ResponseDTO<?>> getUserById(@RequestParam(required = false) Long id,
                                                      @RequestParam(required = false) String email){
        return ResponseEntity.ok(new ResponseDTO<>(200,userService.getUserByIdOrEmail(id,email)));
    }

    @GetMapping("/get/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<?>> getUserByRole(@RequestParam(required = true) Role role){
        if(role!=null){
            return ResponseEntity.ok(new ResponseDTO<>(userService.getByRole(role)));
        }
        throw new DetailMissingException();
    }

    @PatchMapping("/change/role/admin")
    public ResponseEntity<ResponseDTO<?>> changeRoleToAdmin(@RequestParam(required = true)Long id){
        return ResponseEntity.ok(new ResponseDTO<>(userService.changeRoleAsAdmin(id)));
    }


}
