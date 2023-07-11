package modo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final Environment environment;

    @GetMapping("/profile")
    public String getProfile() {
        List<String> profile = Arrays.asList(environment.getActiveProfiles());
        List<String> realProfile = Arrays.asList("real1", "real2");
        String defaultProfile = profile.isEmpty() ? "default" : profile.get(0);

        return profile.stream()
                .filter(realProfile::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
