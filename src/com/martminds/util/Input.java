package com.martminds.util;

import java.util.Scanner;

public class Input 
{
    public static final Scanner sc = new Scanner(System.in);

    public static String promptString(String message) 
    {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static int promptInt(String message) 
    {
        while (true) 
            {
            System.out.print(message);
            try 
            {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Please enter a number greater than 0.");
            } 
            catch (NumberFormatException e) 
            {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
