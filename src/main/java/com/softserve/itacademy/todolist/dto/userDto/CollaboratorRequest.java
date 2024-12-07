package com.softserve.itacademy.todolist.dto.userDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorRequest {

    @JsonProperty("collaborator_id")
    private long collaborator_id;

}
