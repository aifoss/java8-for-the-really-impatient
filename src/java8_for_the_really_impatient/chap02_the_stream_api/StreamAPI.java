package java8_for_the_really_impatient.chap02_the_stream_api;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * Created by sofia on 12/26/16.
 */
public class StreamAPI {

    private static Stream<Character> characterStream(String s) {
        List<Character> result = new ArrayList<>();
        for (char c : s.toCharArray()) result.add(c);
        return result.stream();
    }


    private static class T {
        Optional<T> f() { return Optional.empty(); }
        Optional<U> g() { return Optional.empty(); }
    }

    private static class U {

    }

    private static Optional<U> composeOptionals(T s) {
        return s.f().flatMap(T::g);
    }


    private static Optional<Double> inverse(Double x) {
        return x == 0 ? Optional.empty() : Optional.of(1/x);
    }

    private static Optional<Double> squareRoot(Double x) {
        return x < 0 ? Optional.empty() : Optional.of(Math.sqrt(x));
    }


    private static class Person {
        int id;
        String name;

        public Person() {}

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }


    private static class City {
        String state;
        String name;
        int population;

        public City() {}

        public City(String state, String name, int population) {
            this.state = state;
            this.name = name;
            this.population = population;
        }

        public String getState() { return state; }
        public String getName() { return name; }
        public int getPopulation() { return population; }
    }



    public static void main(String... args) throws IOException {
//        String contents = new String(Files.readAllBytes(Paths.get("alice.txt")), StandardCharsets.UTF_8);
        String contents = "bla bla blah";
        String[] words = contents.split(" ");
        List<String> wordList = Arrays.asList(words);

        long count = wordList.stream().filter(w -> w.length() > 12).count();
        count = wordList.parallelStream().filter(w -> w.length() > 12).count();

        Stream<String> wordStream = Stream.of(words);
        Stream<String> song = Stream.of("gently", "down", "the", "stream");
        Stream<String> silence = Stream.empty();
        Stream<String> partial = Arrays.stream(words, 0, 3);
        Stream<String> splitStream = Pattern.compile(" ").splitAsStream(contents);
        Stream<String> skipStream = Stream.of(words).skip(3);

        Stream<String> echos = Stream.generate(() -> "Echo");
        Stream<Double> randoms = Stream.generate(Math::random).limit(100);

        Stream<BigInteger> integers = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.ONE));

//        Path path = Paths.get("alice.txt");
//        try (Stream<String> lines = Files.lines(path)) {
//
//        }

        Stream<String> wordListStream = wordList.stream();
        Stream<String> longWords = wordListStream.filter(w -> w.length() > 12);
        Stream<String> lowercaseWords = wordList.stream().map(String::toLowerCase);
        Stream<Character> firstChars = wordList.stream().map(s -> s.charAt(0));

        Stream<Stream<Character>> streamOfCharStreams = wordList.stream().map(w -> characterStream(w));
        Stream<Character> letters = wordList.stream().flatMap(w -> characterStream(w));

        Stream<Character> combined = Stream.concat(
                characterStream("Hello"), characterStream("World")
        );

        Object[] powers = Stream.iterate(1.0, p -> p * 2)
                .peek(e -> System.out.println("Fetching "+e))
                .limit(20)
                .toArray();

        Stream<String> uniqueWords = Stream.of("merrily", "merrily", "merrily", "gently")
                .distinct();

        Stream<String> longestFirst = wordList.stream().sorted(Comparator.comparing(String::length).reversed());

        Optional<String> largest = wordList.stream().max(String::compareToIgnoreCase);
        if (largest.isPresent()) {
            System.out.println("Largest: "+largest.get());
        }

        Predicate<String> startsWith = s -> s.startsWith("Q");

        Optional<String> startsWithQ = wordList.stream().filter(startsWith).findFirst();
        Optional<String> startsWithQAny = wordList.stream().filter(startsWith).findAny();

        boolean aWordStartsWithQ = wordList.stream().anyMatch(startsWith);
        boolean allWordsStartWithQ = wordList.stream().allMatch(startsWith);
        boolean noWordsStartWithQ = wordList.stream().noneMatch(startsWith);

        List<String> matches = new ArrayList<>();

        startsWithQ.ifPresent(v -> matches.add(v));
        startsWithQAny.ifPresent(matches::add);

        Optional<Boolean> added = startsWithQAny.map(matches::add);

        String res = startsWithQAny.orElse("default");
        res = startsWithQAny.orElseGet(() -> System.getProperty("user.dir"));
//        res = startsWithQAny.orElseThrow(NoSuchElementException::new);

        Optional<String> nullableRes = Optional.ofNullable(System.getProperty("admin.dir"));

        double x = 100;
        Double sqrtOfInverse = inverse(x).flatMap(StreamAPI::squareRoot).get();
        Double sqrtOfInverse2 = Optional.of(x).flatMap(StreamAPI::inverse).flatMap(StreamAPI::squareRoot).get();

        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> valueStream = values.stream();
        Optional<Integer> sumOptional = valueStream.reduce((a, b) -> a + b);

        sumOptional = values.stream().reduce(Integer::sum);
        Integer sum = values.stream().reduce(0, Integer::sum);

        int sumOfWordLengths = wordList.stream().map(s -> s.length()).reduce(0, Integer::sum);
        sumOfWordLengths = wordList.stream().mapToInt(String::length).sum();

        String[] wordArray = wordList.stream().toArray(String[]::new);
        Set<String> wordSet = wordList.stream().collect(HashSet::new, HashSet::add, HashSet::addAll);
        wordSet = wordList.stream().collect(Collectors.toSet());
        TreeSet<String> wordTreeSet = wordList.stream().collect(Collectors.toCollection(TreeSet::new));

        String joined = wordList.stream().collect(Collectors.joining());
        joined = wordList.stream().collect(Collectors.joining(", "));

        Object[] objects = new Object[2];
        objects[0] = new Object();
        objects[1] = new Object();
        Stream<Object> objectStream = Stream.of(objects);
        joined = objectStream.map(Object::toString).collect(Collectors.joining(", "));

        IntSummaryStatistics summary = wordList.stream().collect(
                Collectors.summarizingInt(String::length));
        double averageWordLength = summary.getAverage();
        double maxWordLength = summary.getMax();

        wordList.stream().forEach(System.out::println);
        wordList.stream().forEachOrdered(System.out::println);

        Person[] persons = new Person[2];
        persons[0] = new Person(0, "Paul");
        persons[1] = new Person(1, "Tom");
        Stream<Person> people = Stream.of(persons);
        List<Person> personList = Arrays.asList(persons);
        Map<Integer, String> idToName = people.collect(Collectors.toMap(Person::getId, Person::getName));
        Map<Integer, Person> idToPerson = personList.stream().collect(Collectors.toMap(Person::getId, Function.identity()));

        Locale[] availableLocales = Locale.getAvailableLocales();
        List<Locale> locales = Arrays.asList(availableLocales);
        Stream<Locale> localeStream = Stream.of(availableLocales);

        Map<String, String> languageNames = localeStream.collect(
                Collectors.toMap(
                        l -> l.getDisplayLanguage(),
                        l -> l.getDisplayLanguage(),
                        (existingValue, newValue) -> existingValue));


        Map<String, Set<String>> countryToLanguageSets = locales.stream().collect(
                Collectors.toMap(
                        l -> l.getDisplayCountry(),
                        l -> Collections.singleton(l.getDisplayLanguage()),
                        (a, b) -> {
                            Set<String> r = new HashSet<>(a);
                            r.addAll(b);
                            return r;
                        }));

        idToPerson = personList.stream().collect(
                Collectors.toMap(
                        Person::getId,
                        Function.identity(),
                        (existingValue, newValue) -> { throw new IllegalStateException(); },
                        TreeMap::new));

        Map<String, List<Locale>> countryToLocales = locales.stream().collect(
                Collectors.groupingBy(Locale::getCountry));
        List<Locale> swissLocales = countryToLocales.get("CH");

        Map<Boolean, List<Locale>> englishAndOtherLocales = locales.stream().collect(
                Collectors.partitioningBy(l -> l.getLanguage().equals("en")));
        List<Locale> englishLocales = englishAndOtherLocales.get(true);

        ConcurrentMap<String, List<Locale>> countryToLocalesConcurrent = locales.stream().collect(
                Collectors.groupingByConcurrent(Locale::getCountry));

        Map<String, Set<Locale>> countryToLocaleSets = locales.stream().collect(
                Collectors.groupingBy(Locale::getCountry, Collectors.toSet()));

        Map<String, Long> countryToLocaleCounts = locales.stream().collect(
                Collectors.groupingBy(Locale::getCountry, Collectors.counting()));

        List<City> cities = Arrays.asList(
                new City("NY", "New York", 100), new City("CA", "Los Angeles", 50), new City("PA", "Philadelphia", 70));

        Map<String, Integer> stateToCityPopulation = cities.stream().collect(
                Collectors.groupingBy(City::getState, Collectors.summingInt(City::getPopulation)));

//        Map<String, City> stateToLargestCity = cities.stream().collect(
//                Collectors.groupingBy(City::getState, Collectors.maxBy(Comparator.comparingInt(City::getPopulation))));

        Map<String, Optional<String>> stateToLongestCityName = cities.stream().collect(
                Collectors.groupingBy(City::getState,
                        Collectors.mapping(City::getName,
                                Collectors.maxBy(Comparator.comparing(String::length))))
        );

        countryToLanguageSets = locales.stream().collect(
                Collectors.groupingBy(l -> l.getDisplayCountry(),
                        Collectors.mapping(l -> l.getDisplayLanguage(),
                                Collectors.toSet())));

        Map<String, IntSummaryStatistics> stateToCityPopulationSummary = cities.stream().collect(
                Collectors.groupingBy(City::getState,
                        Collectors.summarizingInt(City::getPopulation)));

        Map<String, String> stateToCityNames = cities.stream().collect(
                Collectors.groupingBy(City::getState,
                        Collectors.reducing("", City::getName,
                                (s, t) -> s.length() == 0 ? t : s + ", " + t)));

        stateToCityNames = cities.stream().collect(
                Collectors.groupingBy(City::getState,
                        Collectors.mapping(City::getName,
                                Collectors.joining(", "))));

        IntStream intStream = IntStream.of(1, 1, 2, 3, 5);
        int[] intValues = { 1, 1, 2, 3, 5 };
        intStream = Arrays.stream(intValues, 0, 3);

        IntStream zeroToNinetyNine = IntStream.range(0, 100);
        IntStream zeroToHundred = IntStream.rangeClosed(0, 100);

        String sentence = "\uD835\uDD46 is the set of octonions.";
        IntStream codes = sentence.codePoints();

        IntStream wordLengths = wordList.stream().mapToInt(String::length);
        Stream<Integer> wordLengthsBoxed = wordLengths.boxed();
        Stream<Integer> integerStream = IntStream.range(0, 100).boxed();

        OptionalInt min = zeroToHundred.min();

        zeroToHundred = IntStream.rangeClosed(0, 100);
        OptionalInt max = zeroToHundred.max();

        zeroToHundred = IntStream.rangeClosed(0, 100);
        long sum2 = zeroToHundred.sum();

        zeroToHundred = IntStream.rangeClosed(0, 100);
        OptionalDouble average = zeroToHundred.average();

        int minVal = min.getAsInt();
        int maxVal = max.getAsInt();
        double avgVal = average.getAsDouble();

        zeroToHundred = IntStream.rangeClosed(0, 100);
        IntSummaryStatistics zeroToHundredSummary = zeroToHundred.summaryStatistics();

        minVal = zeroToHundredSummary.getMin();
        maxVal = zeroToHundredSummary.getMax();
        sum2 = zeroToHundredSummary.getSum();
        avgVal = zeroToHundredSummary.getAverage();

        Random random = new Random();
        IntStream randomIntegers = random.ints();
        LongStream randomLongs = random.longs();
        DoubleStream randomDoubles = random.doubles();

        Stream<String> parallelWords = Stream.of(words).parallel();

        Stream<Integer> infiniteIntegers = Stream.generate(random::nextInt);
        Stream<Integer> sample = infiniteIntegers.parallel().unordered().limit(10);

//        Map<String, List<String>> citiesByState = cities.stream().parallel().collect(
//                Collectors.groupingByConcurrent(City::getState));
    }

}
