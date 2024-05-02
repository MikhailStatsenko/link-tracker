package edu.java.scrapper.model;

import edu.java.scrapper.model.converter.UriToStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "link")
@Data
@NoArgsConstructor
public class Link {
    @Id
    @SequenceGenerator(name = "link_id_seq", sequenceName = "link_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_id_seq")
    private Integer id;

    @NotNull
    @Column(name = "url")
    @Convert(converter = UriToStringConverter.class)
    private URI url;

    @NotNull
    @Column(name = "last_check_time")
    private OffsetDateTime lastCheckTime;

    @ManyToMany(mappedBy = "trackedLinks")
    private List<Chat> chats;

    public Link(URI url) {
        this.url = url;
        lastCheckTime = OffsetDateTime.now();
    }
}
