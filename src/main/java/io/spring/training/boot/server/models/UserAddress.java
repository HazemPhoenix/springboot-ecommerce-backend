package io.spring.training.boot.server.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {
    @Id
    private Long id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    private String street;
    private String city;
    private String country;
    private String zip;

    public UserAddress(User user, String street, String city, String country, String zip) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.country = country;
        this.zip = zip;
    }
}
