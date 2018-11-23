package java8_for_the_really_impatient.chap01_lambda_expressions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sofia on 12/26/16.
 */
public class LambdaExpressions {

    class Greeter {
        public void greet() {
            System.out.println("Hello world!");
        }
    }

    class ConcurrentGreeter extends Greeter {
        @Override
        public void greet() {
            Thread t = new Thread(super::greet);
            t.start();
        }
    }

    public static void repeatMessage(String text, int count) {
        Runnable r = () -> {
            for (int i = 0; i < count; i++) {
                System.out.println(text);
                Thread.yield();
            }
        };
        new Thread(r).start();
    }

    class Application {
        public void doWork() {
            Runnable runner = () -> { System.out.println(this.toString()); };
        }
    }

    interface Person {
        long getId();
        default String getName() { return "John Q. Public"; }
    }

    interface Named {
        default String getName() { return getClass().getName()+"_"+hashCode(); }
    }

    class Student implements Person, Named {

        public Student() {}

        @Override
        public long getId() { return 0; }

        @Override
        public String getName() { return Person.super.getName(); }
    }


    public static void main(String... args) {
        Comparator<String> comp = (s1, s2) -> Integer.compare(s1.length(), s2.length());
        Comparator<String> comp2 = (s1, s2) -> {
            if (s1.length() < s2.length()) return -1;
            else if (s1.length() > s2.length()) return 1;
            else return 0;
        };

        EventHandler<ActionEvent> listener = event -> System.out.println("Thanks for clicking!");

//        Button button = new Button();
//        button.setOnAction(event -> System.out.println("Thanks for clicking!"));
//        button.setOnAction(event -> System.out.println(button));
//        button.setOnAction(System.out::println);

        String[] words = { "blah", "bong", "bi" };
        Arrays.sort(words, (w1, w2) -> Integer.compare(w1.length(), w2.length()));
        Arrays.sort(words, Comparator.comparing(String::length));

        List<String> wordList = Arrays.asList(words);
        wordList.sort(comp);
        wordList.sort(comp2);
        wordList.sort(Comparator.comparing(String::length));

        Runnable sleeper = () -> {
            try {
                System.out.println("Zzz");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Arrays.sort(words, String::compareToIgnoreCase);

//        List<String> labels = Arrays.asList("l1", "l2");
//        Stream<Button> stream = labels.stream().map(Button::new);
//        List<Button> buttons = stream.collect(Collectors.toList());
//        Button[] buttonArray = stream.toArray(Button[]::new);

//        buttons.forEach(System.out::println);
    }

}
