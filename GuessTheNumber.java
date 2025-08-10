import java.util.Random;
import java.util.Scanner;

class GuessTheNumber {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        int rounds = 3; // Number of rounds to play
        int maxAttempts = 5; // Number of attempts per round
        int totalScore = 0;

        System.out.println("=== Welcome to Guess the Number Game! ===");

        for (int round = 1; round <= rounds; round++) {
            int numberToGuess = rand.nextInt(100) + 1; // [1,100]
            int attempts = 0;
            boolean guessed = false;

            System.out.println("\nROUND " + round + ": Guess a number between 1 and 100.");

            while (attempts < maxAttempts) {
                System.out.print("Attempt " + (attempts + 1) + ": Enter your guess: ");
                int userGuess = sc.nextInt();
                attempts++;

                if (userGuess == numberToGuess) {
                    int points = (maxAttempts - attempts + 1) * 10; // More points for fewer attempts
                    totalScore += points;
                    System.out.println("Congratulations! You guessed the number in " + attempts + " attempts.");
                    System.out.println("You earned " + points + " points this round.");
                    guessed = true;
                    break;
                } else if (userGuess < numberToGuess) {
                    System.out.println("The number is higher!");
                } else {
                    System.out.println("The number is lower!");
                }
            }

            if (!guessed) {
                System.out.println("Sorry! You've used all attempts. The number was: " + numberToGuess);
            }
        }

        System.out.println("\n=== Game Over! Your total score: " + totalScore + " ===");
        sc.close();
    }
}
