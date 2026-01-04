package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Id
    private User user;
    private String street;
    private String city;
    private String country;
    private String zip;
}
