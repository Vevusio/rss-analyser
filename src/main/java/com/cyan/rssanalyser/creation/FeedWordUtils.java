package com.cyan.rssanalyser.creation;

import com.stoyanr.util.CharPredicate;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

@Service
class FeedWordUtils {
    private final Environment environment;

    private Pattern defaultWordPattern;
    private final Map<String, List<String>> languageWordBlacklist = Collections.synchronizedMap(new HashMap<>());

    public FeedWordUtils(Environment environment) {
        String defaultWordRegex = environment.getProperty("analyser.defaultWordRegex");
        this.defaultWordPattern = Pattern.compile(defaultWordRegex);

        this.environment = environment;
    }

    public CharPredicate retrieveCharacterPredicate() {
        return c -> (Character.isLetterOrDigit(c) || c == '-');
    }

    public boolean isValidWord(String word, @Nullable String language) {
        Matcher matcher = defaultWordPattern.matcher(word);
        if (!matcher.matches()) {
            return false;
        }

        List<String> blacklist = retrieveWordBlacklist(language);
        boolean isBlacklisted = blacklist.contains(word);
        return !isBlacklisted;
    }

    private List<String> retrieveWordBlacklist(@Nullable String language) {
        if (language == null) {
            return emptyList();
        }

        String languageBlacklistKey = "analyser.localization." + language.toLowerCase() + ".blacklist";
        synchronized (languageWordBlacklist) {
            if (!languageWordBlacklist.containsKey(languageBlacklistKey)) {
                String[] blacklistWords = environment.getProperty(languageBlacklistKey, "").split(",");
                languageWordBlacklist.put(language, Arrays.asList(blacklistWords));
            }
        }

        return languageWordBlacklist.getOrDefault(language, emptyList());
    }
}
