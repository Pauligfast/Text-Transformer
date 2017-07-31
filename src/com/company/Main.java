package com.company;


import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

interface TextTransformer {
    String transform(String str);
}

interface OptionHandler {
    void setOptions(String[] options);
}

class SetRegexTextTransformer extends RegexTextTransformer implements OptionHandler {

    @Override
    public void setOptions(String[] options) {
        regex = options[0];
        replacement = options[1];
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public String getReplacement() {
        return replacement;
    }


}

abstract class RegexTextTransformer implements TextTransformer {

    String regex;
    String replacement;

    @Override
    public String transform(String str) {
        return str.replaceAll(getRegex(), getReplacement());
    }

    public abstract String getRegex();

    public abstract String getReplacement();
}

class HtmlTabReplacer implements TextTransformer, OptionHandler {

    private int nbspCounter = 2;

    @Override
    public String transform(String str) {
        String replacement = new String(new char[nbspCounter]).replace("\0", "&nbsp;");
        return str.replaceAll("\t", replacement).replaceAll("    ", replacement);
    }

    @Override
    public void setOptions(String[] options) {
        nbspCounter = Integer.valueOf(options[0]);
    }
}

class CaseChanger implements TextTransformer, OptionHandler {

    private String regex;

    @Override
    public String transform(String str) {
        if (Objects.equals(regex, "l")) return str.toUpperCase();
        else return str.toLowerCase();
    }

    @Override
    public void setOptions(String[] options) {
        regex = options[0];
    }
}


public class Main {
    private enum TextTransformers {
        REGEX(SetRegexTextTransformer.class),
        HTMLTAB(HtmlTabReplacer.class),
        CASE(CaseChanger.class);
        public final Class<? extends TextTransformer> clazz;

        TextTransformers(Class<? extends TextTransformer> clazz) {
            this.clazz = clazz;
        }
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        try (Scanner s = new Scanner(System.in)) {
            s.useDelimiter("\\A");
            TextTransformer transformer = TextTransformers.valueOf(args[0]).clazz.newInstance();
            //get(args[0]).newInstance() jesli chcemy podawac nazwę bezpośrednio;
            if (transformer instanceof OptionHandler) {
                ((OptionHandler) transformer).setOptions(Arrays.copyOfRange(args, 1, args.length));
            }
            System.out.println(transformer.transform(s.hasNext() ? s.next() : ""));
        }
    }

}