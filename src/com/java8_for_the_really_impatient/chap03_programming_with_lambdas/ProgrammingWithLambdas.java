package com.java8_for_the_really_impatient.chap03_programming_with_lambdas;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by sofia on 12/26/16.
 */
public class ProgrammingWithLambdas {

    public static void info(Logger logger, Supplier<String> message) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(message.get());
        }
    }

    public static void repeat(int n, Runnable action) {
        for (int i = 0; i < n; i++) {
            action.run();
        }
    }

    public static void repeat2(int n, IntConsumer action) {
        for (int i = 0; i < n; i++) {
            action.accept(i);
        }
    }

    public static Image transform(Image in, UnaryOperator<Color> f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                out.getPixelWriter().setColor(x, y,
                        f.apply(in.getPixelReader().getColor(x, y)));
            }
        }
        return out;
    }

    public static UnaryOperator<Color> brighten(double factor) {
        return c -> c.deriveColor(0, 1, factor, 1);
    }


    @FunctionalInterface
    public interface ColorTransformer {
        Color apply(int x, int y, Color colorAtXY);
    }

    public static <T> UnaryOperator<T> compose(UnaryOperator<T> op1, UnaryOperator<T> op2) {
        return t -> op2.apply(op1.apply(t));
    }

    public static class LatentImage {
        private Image in;
        private List<UnaryOperator<Color>> pendingOperations;

        public LatentImage from(Image image) {
            in = image;
            return this;
        }

        public LatentImage transform(UnaryOperator<Color> f) {
            pendingOperations.add(f);
            return this;
        }

        public Image toImage() {
            int width = (int) in.getWidth();
            int height = (int) in.getHeight();
            WritableImage out = new WritableImage(width, height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color c = in.getPixelReader().getColor(x, y);
                    for (UnaryOperator<Color> f : pendingOperations) {
                        c = f.apply(c);
                    }
                    out.getPixelWriter().setColor(x, y, c);
                }
            }
            return out;
        }
    }

    public static Color[][] parallelTransform(Color[][] in, UnaryOperator<Color> f) {
        int n = Runtime.getRuntime().availableProcessors();
        int height = in.length;
        int width = in[0].length;
        Color[][] out = new Color[height][width];

        try {
            ExecutorService pool = Executors.newCachedThreadPool();

            for (int i = 0; i < n; i++) {
                int fromY = i * height / n;
                int toY = (i+1) * height / n;

                pool.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        for (int y = fromY; y < toY; y++) {
                            out[y][x] = f.apply(in[y][x]);
                        }
                    }
                });
            }

            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.HOURS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static void doInOrder(Runnable first, Runnable second) {
        first.run();
        second.run();
    }

    public static void doInOrderAsync(Runnable first, Runnable second, Consumer<Throwable> handler) {
        Thread t = new Thread() {
            public void run() {
                try {
                    first.run();
                    second.run();
                } catch (Throwable t) {
                    handler.accept(t);
                }
            }
        };
        t.start();
    }

    public static <T> void doInOrderAsync(Supplier<? extends T> first, Consumer<? super T> second, Consumer<? super Throwable> handler) {
        Thread t = new Thread() {
            public void run() {
                try {
                    T result = first.get();
                    second.accept(result);
                } catch (Throwable t) {
                    handler.accept(t);
                }
            }
        };
        t.start();
    }

    public static <T> Supplier<T> unchecked(Callable<T> f) {
        return () -> {
            try {
                return f.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } catch (Throwable t) {
                throw t;
            }
        };
    }


    public static void main(String... args) {
        repeat(5, () -> System.out.println("Hello, World!"));

        System.out.println();

        repeat2(5, i -> System.out.println("Countdown: " + (5-i)));

        System.out.println();

        int a = 1;
        int b = 2;
        int c = 1;

        Predicate<Object> equal = Predicate.isEqual(a).or(Predicate.isEqual(b));
        boolean res = equal.test(c);

        System.out.println(res);
        System.out.println();

        try {
            Image image = new Image("eiffel-tower.png");
            Image brighterImage = transform(image, Color::brighter);
            Image brightenedImage = transform(image, brighten(1.2));

            Image eiffelTowerImage = new Image("eiffel-tower.png");
            Image finalImage = transform(image, compose(Color::brighter, Color::grayscale));

            LatentImage latent = new LatentImage();
            finalImage = latent.from(image)
                    .transform(Color::brighter)
                    .transform(Color::grayscale)
                    .toImage();

        } catch (IllegalArgumentException e) {

        }

        try {
            Supplier<String> sup = unchecked(() -> new String(Files.readAllBytes(Paths.get("/etc/passwd")), StandardCharsets.UTF_8));

        } catch (Exception e) {

        }

        Stream<String> wordStream = Stream.of("w1", "w2");
        String[] result = wordStream.toArray(String[]::new);
    }

}
