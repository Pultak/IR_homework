package cz.zcu.kiv.nlp.ir;


import java.text.Normalizer;
import java.util.*;

/**
 * Created by Tigi on 29.2.2016.
 */
public class BasicPreprocessing implements Preprocessing {

    Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
    Stemmer stemmer;
    Tokenizer tokenizer;
    Set<String> stopwords = new HashSet<String>(Arrays.asList("ačkoli", "ale", "anebo", "ano", "asi",
            ",", ".", "a", "v", "s", "o", "k", "z", "si", "by",
            "aspoň", "během", "bez", "beze", "blízko", "bohužel", "brzo",
            "co",  "dál", "dále", "daleko", "do",  "docela",
            "hodně","jak", "je","jeho", "její", "jejich", "jemu", "jen", "jenom", "ještě", "jestli", "jestliže",
            "jich", "jimi", "jinak", "jsem", "jsi", "jsme", "jsou", "jste", "kam", "kde", "kdo", "kdy",
            "když", "ke", "kolik", "kromě", "která", "které", "kteří", "který", "kvůli", "má", "mají", "málo", "mám",
            "máme", "máš", "máte", "mé", "mě", "mezi", "mí", "mít", "mně", "mnou", "moc", "mohl", "mohou", "moje",
            "moji", "možná", "můj", "my", "na", "nad", "nade", "nám", "námi", "naproti", "nás", "náš",
            "naše", "naši", "ne", "ně", "nebo", "něco", "nějak", "nejsi", "někde", "někdo", "nemají", "nemáme",
            "nemáte", "neměl", "němu", "není", "nestačí", "nevadí", "než", "nic", "nich", "ním", "nimi", "nula", "od",
            "ode", "on", "ona", "oni", "ono", "ony", "osm", "pak", "po", "pořád", "potom",
            "pozdě", "před", "přes", "přese", "pro", "proč", "prosím", "prostě", "proti", "protože", "rovně", "se",
            "skoro", "snad", "spolu", "sta", "sté", "sto",
            "ta", "tady", "tak", "takhle", "taky", "tam", "tamhle", "tamhleto", "tamto", "tě", "tebe", "tebou",
            "ted'", "tedy", "ten", "ti", "to", "tobě", "tohle", "toto", "třeba",
            "trošku", "tvá", "tvé", "tvoje", "tvůj", "ty", "určitě", "už", "vám", "vámi", "vás", "váš", "vaše",
            "vaši", "ve", "vedle", "vlastně", "vůbec", "vy", "vždy", "za", "zač",
            "zatímco", "ze", "že", "aby", "aj", "ani", "az", "budem", "budes", "by", "byt", "ci", "clanek", "clanku",
             "coz", "cz", "ho", "jako", "jej", "jeji", "jeste", "ji",
            "jine", "jiz", "jses", "kdyz", "ktera", "ktere", "kteri", "kterou", "ktery", "ma", "mate", "mi",
            "muj", "muze", "nam", "napiste", "nas", "nasi", "nejsou", "neni", "nez", "nove", "novy", "pod", "podle",
            "pokud", "pouze", "prave", "pred", "pres", "pri", "proc", "proto", "protoze", "pta", "re", "si",
            "sve", "svych", "svym", "svymi", "take", "takze", "tato", "tema", "tento", "teto", "tim",
            "timtotipytoho", "tohototom", "tomto", "tomuto", "tu", "tuto", "tyto", "uz", "vam", "vas", "vase",
            "vice", "vsak", "zda", "zde", "zpet", "a", "aniž", "až", "být", "což", "či",
            "další", "i", "jenž", "jiné", "již", "jseš", "jšte", "k", "každý", "kteři", "ku", "me", "ná",
            "napište", "nechť", "ní", "nové", "nový", "o", "práve", "první", "přede", "při", "s", "sice", "své",
            "svůj", "svých", "svým", "svými", "také", "takže", "te", "těma", "této", "tím", "tímto", "u", "v",
            "více", "však", "všechen", "z", "zpět", "přičemž"));
    boolean removeAccentsBeforeStemming;
    boolean removeAccentsAfterStemming;
    boolean removeStopWords;
    boolean toLowercase;

    public BasicPreprocessing(Stemmer stemmer, Tokenizer tokenizer, Set<String> stopwords, boolean removeAccentsBeforeStemming, boolean removeAccentsAfterStemming, boolean toLowercase) {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        if(stopwords != null){
            this.stopwords = stopwords;
        }
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
    }

    @Override
    public void index(String document) {
        if (toLowercase) {
            document = document.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            document = removeAccents(document);
        }
        for (String token : tokenizer.tokenize(document)) {
            if(token.length() < 3 || stopwords.contains(token)){
                //this token is a stop word!
                continue;
            }
            if (stemmer != null) {
                token = stemmer.stem(token);
            }

            if (removeAccentsAfterStemming) {
                token = removeAccents(token);
            }
            if (!wordFrequencies.containsKey(token)) {
                wordFrequencies.put(token, 0);
            }

            wordFrequencies.put(token, wordFrequencies.get(token) + 1);
        }
    }

    @Override
    public String getProcessedForm(String text) {
        if (toLowercase) {
            text = text.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            text = removeAccents(text);
        }
        if (stemmer != null) {
            text = stemmer.stem(text);
        }
        if (removeAccentsAfterStemming) {
            text = removeAccents(text);
        }
        return text;
    }

    final String withDiacritics = "áÁčČďĎéÉěĚíÍňŇóÓřŘšŠťŤúÚůŮýÝžŽ";
    final String withoutDiacritics = "aAcCdDeEeEiInNoOrRsStTuUuUyYzZ";

    private String removeAccents(String text) {
        //for (int i = 0; i < withDiacritics.length(); i++) {
            text = Normalizer.normalize(text, Normalizer.Form.NFD);
            text = text.replaceAll("\\p{M}", "");
            //text = text.replaceAll("" + withDiacritics.charAt(i), "" + withoutDiacritics.charAt(i));
        //}
        return text;
    }

    public Map<String, Integer> getWordFrequencies() {
        return wordFrequencies;
    }
}
