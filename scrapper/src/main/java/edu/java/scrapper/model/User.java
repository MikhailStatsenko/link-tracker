package edu.java.scrapper.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long chatId;
    private List<Link> trackedLinks;
}
