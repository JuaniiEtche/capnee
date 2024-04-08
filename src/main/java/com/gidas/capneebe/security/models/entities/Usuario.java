package com.gidas.capneebe.security.models.entities;

import com.gidas.capneebe.global.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET deleted_at = current_timestamp() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(length = 45, nullable = false)
    private String usuario;

    @Column(length = 45, nullable = false)
    private String nombre;

    @Column(length = 45, nullable = false)
    private String apellido;

    @Column(length = 45, nullable = false)
    private long legajo;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(length = 300, nullable = false)
    private String contraseña;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    public String getFullName() {
        return nombre + ", " + apellido;
    }

}
