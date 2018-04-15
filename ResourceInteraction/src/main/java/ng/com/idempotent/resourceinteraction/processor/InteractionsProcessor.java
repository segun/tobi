/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.com.idempotent.resourceinteraction.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import ng.com.idempotent.resourceinteraction.MainFrame;

/**
 *
 * @author aardvocate
 */
public class InteractionsProcessor {

    private static int N;
    private static List<String> requirementsLine1;
    private static List<String> requirementsLine2;
    private static List<String> requirementsLine3;
    private static List<String> requirements;
    private static List<String> codes;
    private static HashMap<String, Long> dfidMap = new HashMap<>();
    private static String interactions = "";

    private static final String[] res1KW = {
        "User", "Access", "Role", "Right", "Permission", "Public", "Private"
    };

    private static final String[] res2KW = {
        "Workgroup", "group"
    };

    private static final String[] res3KW = {
        "file", "collection"
    };

    private static final String[] res4KW = {
        "Metadata", "element", "record"
    };

    public static int resource;

    public static String getInteractions() {
        return interactions;
    }

    //TODO: 
    //Combine lines 2 and 3
    public static boolean findInteractions(MainFrame mainFrame) {
        interactions = "";
        requirementsLine3 = FileProcessor.getFileLines()
                .stream().filter(x -> {
                    return x.startsWith("So that: ")
                            || x.startsWith("So that:");
                }).map(x -> x.replace("So that: ", "").replace("So that:", "").replace(",", ""))
                .collect(Collectors.toList());

        requirementsLine2 = FileProcessor.getFileLines()
                .stream().filter(x -> {
                    return x.startsWith("I want: ")
                            || x.startsWith("I want to: ");
                }).map(x -> x.replace("I want: ", "").replace("I want to: ", "").replace(",", ""))
                .collect(Collectors.toList());

        requirementsLine1 = FileProcessor.getFileLines()
                .stream().filter(x -> {
                    return x.startsWith("As a: ");
                }).map(x -> x.replace("As a: ", "").replace(",", ""))
                .collect(Collectors.toList());

        codes = FileProcessor.getFileLines()
                .stream().filter(x -> x.startsWith("US")).map(x -> x.replaceAll(":.*", "").replace("US", ""))
                .collect(Collectors.toList());
        System.err.println(codes.size());

        requirements = new ArrayList<>();
        for (int i = 0; i < requirementsLine1.size(); i++) {
            String combined = /**requirementsLine1.get(i).trim() + " " + */requirementsLine2.get(i).trim() + " " + requirementsLine3.get(i).trim();
            requirements.add(combined);
        }

        int size = requirements.size();
        N = size;

        assert N == 47;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                String Q = requirements.get(i);
                String W = requirements.get(j);

                HashMap<String, Integer> qWordCounts = occurences(Q);
                HashMap<String, Integer> wWordCounts = occurences(W);

                Set<String> intersection = new HashSet<>();
                intersection.addAll(qWordCounts.keySet());
                intersection.retainAll(wWordCounts.keySet());
                Set<String> inter = intersection.stream().filter(x -> x.length() > 1).collect(Collectors.toSet());
                String pair = codes.get(i) + ":" + codes.get(j);

                //System.err.println(pair + " - " + intersection);
                checkInteraction(inter, qWordCounts, wWordCounts, pair, Q);
            }
        }

        mainFrame.setStatus("Found : " + interactions.split("\n").length + " interactions");
        return true;
    }

    public static HashMap<String, Integer> occurences(String line) {
        HashMap<String, Integer> wordCount = new HashMap<>();

        Pattern.compile(" ").splitAsStream(line).forEach(x -> {
            if (wordCount.containsKey(x)) {
                wordCount.put(x, wordCount.get(x) + 1);
            } else {
                wordCount.put(x, 1);
            }
        });

        return wordCount;
    }

    private static void checkInteraction(Set<String> intersection, HashMap<String, Integer> qWordCounts, HashMap<String, Integer> wWordCounts, String pair, String Q) {
        System.err.println(pair + " : " + intersection);
        double quotient = intersection.stream().map(x -> {
            double tfiq = (qWordCounts.get(x) * 1.0) / qWordCounts.size();
            double tfiw = (wWordCounts.get(x) * 1.0) / wWordCounts.size();

            System.err.println(x + ", " + pair + " : " + tfiq);
            System.err.println(x + ", " + pair + " : " + tfiw);

            long dfid;
            if (dfidMap.containsKey(x)) {
                dfid = dfidMap.get(x);
            } else {
                dfid = requirements.stream().map(y -> {
                    return y.contains(x) ? 1 : 0;
                }).collect(Collectors.summarizingInt(Number::intValue)).getSum();
                dfidMap.put(x, dfid);
            }

            System.err.println(x + ", " + pair + " : " + dfid);

            double idf = Math.log10(N / (dfid + 1));
            System.err.println(x + ", " + pair + " : " + idf);

//            tfiq = 1 + Math.log10(tfiq);
//            tfiw = 1 + Math.log10(tfiw);
            return tfiq * idf * tfiw * idf;
        }).collect(Collectors.summarizingDouble(Number::doubleValue)).getSum();

        if (quotient == 0) {
            return;
        }

        double sumQSquared = intersection.stream().map(x -> {
            double tfiq = (qWordCounts.get(x) * 1.0) / qWordCounts.size();

            long dfid;
            if (dfidMap.containsKey(x)) {
                dfid = dfidMap.get(x);
            } else {
                dfid = requirements.stream().map(y -> {
                    return y.contains(x) ? 1 : 0;
                }).collect(Collectors.summarizingInt(Number::intValue)).getSum();
                dfidMap.put(x, dfid);
            }

//            tfiq = 1 + Math.log10(tfiq);
            double idf = Math.log10(N / (dfid + 1));
            return (tfiq * idf) * (tfiq * idf);
        }).collect(Collectors.summarizingDouble(Number::doubleValue)).getSum();

        if (sumQSquared == 0) {
            return;
        }
        double sumWSquared = intersection.stream().map(x -> {
            double tfiw = (wWordCounts.get(x) * 1.0) / wWordCounts.size();

            long dfid;
            if (dfidMap.containsKey(x)) {
                dfid = dfidMap.get(x);
            } else {
                dfid = requirements.stream().map(y -> {
                    return y.contains(x) ? 1 : 0;
                }).collect(Collectors.summarizingInt(Number::intValue)).getSum();
                dfidMap.put(x, dfid);
            }

//            tfiw = 1 + Math.log10(tfiw);
            double idf = Math.log10(N / (dfid + 1));
            return (tfiw * idf) * (tfiw * idf);
        }).collect(Collectors.summarizingDouble(Number::doubleValue)).getSum();

        if (sumWSquared == 0) {
            return;
        }
        double interactionValue = quotient / (Math.sqrt(sumQSquared * sumWSquared));

        double th = 0.95;
        double low = 0.85;
        if (resource == 1) {

            for (String res1KW1 : res1KW) {
                if (Q.toLowerCase().contains(res1KW1.toLowerCase())) {
                    th = low;
                    break;
                }
            }
        }

        if (resource == 2) {
            for (String res2KW1 : res2KW) {
                if (Q.toLowerCase().contains(res2KW1.toLowerCase())) {
                    th = low;
                    break;
                }
            }
        }

        if (resource == 3) {
            for (String res3KW1 : res3KW) {
                if (Q.toLowerCase().contains(res3KW1.toLowerCase())) {
                    th = low;
                    break;
                }
            }
        }

        if (resource == 4) {
            for (String res4KW1 : res4KW) {
                if (Q.toLowerCase().contains(res4KW1.toLowerCase())) {
                    th = low;
                    break;
                }
            }
        }

        System.err.println(pair + " " + interactionValue + " " + th);
        if (interactionValue >= th) {
            interactions += pair;
            interactions += "\n";
        }
    }
}
