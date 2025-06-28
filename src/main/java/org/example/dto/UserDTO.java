package org.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    @NotBlank(message = "Поле 'name' не может быть пустым")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Поле 'name' не должно содержать цифр или символов")
    private String name;
    @NotBlank(message = "Поле 'email' не может быть пустым")
    private String email;
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 120, message = "Возраст не может превышать 120")
    private int age;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
