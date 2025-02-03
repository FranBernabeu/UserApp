package com.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    private String username;

    private String name;
    private String email;
    private String gender;
    private String picture;
    private String country;
    private String state;
    private String city;

}
