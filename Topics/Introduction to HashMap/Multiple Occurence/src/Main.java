import java.util.*;
import java.util.stream.Collectors;

class Main {
    private static void printMostFrequentWord(String[] words) {
        // write your code here
        List<String> wordsList = Arrays.asList(words);

        Map<String, Long> wordCounts = wordsList.stream()
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        long max = Collections.max(wordCounts.values());
        wordCounts.entrySet().stream()
                .filter(e -> e.getValue().equals(max))
                .limit(1)
                .forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
    }

    // don't change the code below
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] words = scanner.nextLine().split(" ");
        printMostFrequentWord(words);
    }
}