package portfolio2.module.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import portfolio2.module.account.Account;
import portfolio2.module.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private boolean isChecked;

    @ManyToOne
    private Account account;

    @ManyToMany
    private Set<Tag> commonTag = new HashSet<>();

    private LocalDateTime createdDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}