package cpe;

import java.util.Scanner;

public class practice {
    
    public static void main(String[] args) {
        System.out.println("Code successfully pulled from GitHub!");
        System.out.println("-------------------------------------");
        
        // Setting up input 
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter a whole number: ");
        int userNumber = input.nextInt();
        
        // The core logic structure is identical to procedural languages
        if (userNumber % 2 == 0) {
            System.out.println(userNumber + " is an EVEN number.");
        } else {
            System.out.println(userNumber + " is an ODD number.");
        }
        
        input.close();
    }
}
