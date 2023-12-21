import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

class Main {
    private static void printCommon(int[] firstArray, int[] secondArray) {
        // Enter your Code Here
        Map<Integer, Long> map = Arrays.stream(firstArray)
                .boxed()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        Arrays.stream(secondArray)
                .filter(i -> map.get(i) != null && map.get(i) > 0)
                .peek(i -> map.put(i, map.get(i) - 1))
                .forEach(elem -> System.out.print(elem + " "));
    }

    public static void main(String[] args) {        
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] firstArray = new int [n];
        int[] secondArray = new int [n];
        for (int i = 0; i < n; ++i) { 
            firstArray[i] = scanner.nextInt();
        }
        for (int i = 0; i < n; ++i) { 
            secondArray[i] = scanner.nextInt();
        }

        printCommon(firstArray,secondArray);
    }
}