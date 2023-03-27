package nl.tudelft.cse.sem.user.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@Entity
public abstract class User implements UserDetails {

    static final long serialVersionUID = 1L;

    @Id
    protected String netId;
    protected String password;
    protected String name;
    protected String email;


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
