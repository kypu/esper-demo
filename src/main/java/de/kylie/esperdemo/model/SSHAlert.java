package de.kylie.esperdemo.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@ToString
public class SSHAlert {

    Map<String, Long> attemptsByIpAd;
}
