package modo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = @Index(name = "idx_nickname", columnList = "nickname"))
public class Users implements UserDetails {
    @Id
    @Column
    private String usersId;

    @Column(nullable = false)
    private String nickname;

    @Column
    private Point location;

    @Column(nullable = false)
    private double reviewScore;

    @Column(nullable = false)
    private Long reviewCount;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UsersHistory usersHistory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersReview> usersReviewList = new ArrayList<>();

    public void setUsersHistory(UsersHistory usersHistory) {
        this.usersHistory = usersHistory;
    }

    public void addReview(UsersReview usersReview) {
        // Add usersReview to usersReviewList; join
        this.usersReviewList.add(usersReview);
        // Adjust reviewScore and reviewCount
        this.reviewScore = ((this.reviewCount * this.reviewScore) + usersReview.getScore()) / (double) (this.reviewCount + 1);
        this.reviewCount++;
    }

    public void removeReview(UsersReview usersReview) {
        if (this.reviewCount == 1) {
            this.reviewScore = 0.0;
            this.reviewCount = 0L;
            this.usersReviewList.remove(usersReview);
            return;
        }
        this.reviewScore = ((this.reviewCount * this.reviewScore) - usersReview.getScore()) / (double) (this.reviewCount - 1);
        this.usersReviewList.remove(usersReview);
        this.reviewCount--;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return usersId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
