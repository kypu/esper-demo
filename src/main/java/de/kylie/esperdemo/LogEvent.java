package de.kylie.esperdemo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LogEvent {

    String message;
    String type;

}
