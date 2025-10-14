package com.usabana.market.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clientes")
public class Cliente {

    @Id
    private String id;

    @Column(name = "nombre")
    private String firtsName;

    @Column(name = "apellidos")
    private String lastName;

    @Column(name = "celular")
    private Long cellphone;

    @Column(name = "direccion")
    private String address;

    @Column(name = "correo_electronico")
    private String email;

    @OneToMany(mappedBy = "cliente")
    private List<Compra> compras;
}
