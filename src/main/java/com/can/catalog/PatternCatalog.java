package com.can.catalog;

import com.can.behavirol.chainofresponsibility.ChainOfResponsibilityDemo;
import com.can.behavirol.command.CommandPatternDemo;
import com.can.behavirol.iterator.IteratorPatternDemo;
import com.can.behavirol.mediator.MediatorPatternDemo;
import com.can.behavirol.memento.MementoPatternDemo;
import com.can.behavirol.observer.ObserverPatternDemo;
import com.can.behavirol.state.StatePatternDemo;
import com.can.behavirol.strategy.StrategyPatternDemo;
import com.can.behavirol.templatemethod.TemplateMethodPatternDemo;
import com.can.behavirol.visitor.VisitorPatternDemo;
import com.can.creational.abstractfactory.AbstractFactoryDemo;
import com.can.creational.builder.BuilderDemo;
import com.can.creational.factorymethod.FactoryMethodDemo;
import com.can.creational.prototype.PrototypeDemo;
import com.can.creational.singleton.SingletonDemo;
import com.can.structural.adapter.AdapterPatternDemo;
import com.can.structural.bridge.BridgePatternDemo;
import com.can.structural.composite.CompositePatternDemo;
import com.can.structural.decorator.DecoratorPatternDemo;
import com.can.structural.facade.FacadePatternDemo;
import com.can.structural.flyweight.FlyweightPatternDemo;
import com.can.structural.proxy.ProxyPatternDemo;

import java.io.PrintStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.can.catalog.PatternFamily.BEHAVIORAL;
import static com.can.catalog.PatternFamily.CREATIONAL;
import static com.can.catalog.PatternFamily.STRUCTURAL;

/**
 * Repo içindeki örneklerin tek kaynağıdır.
 *
 * <p>Katalog sayesinde {@link com.can.Main}, somut demo sınıflarını tek tek yönetmek
 * yerine bir aileyi ya da tek bir deseni seçerek çalıştırabilir.</p>
 */
public final class PatternCatalog {

    private static final List<PatternExample> EXAMPLES = List.of(
            example("factory-method", "Factory Method", CREATIONAL, FactoryMethodDemo::run),
            example("abstract-factory", "Abstract Factory", CREATIONAL, AbstractFactoryDemo::run),
            example("builder", "Builder", CREATIONAL, BuilderDemo::run),
            example("prototype", "Prototype", CREATIONAL, PrototypeDemo::run),
            example("singleton", "Singleton", CREATIONAL, SingletonDemo::run),

            example("adapter", "Adapter", STRUCTURAL, AdapterPatternDemo::run),
            example("bridge", "Bridge", STRUCTURAL, BridgePatternDemo::run),
            example("composite", "Composite", STRUCTURAL, CompositePatternDemo::run),
            example("decorator", "Decorator", STRUCTURAL, DecoratorPatternDemo::run),
            example("facade", "Facade", STRUCTURAL, FacadePatternDemo::run),
            example("flyweight", "Flyweight", STRUCTURAL, FlyweightPatternDemo::run),
            example("proxy", "Proxy", STRUCTURAL, ProxyPatternDemo::run),

            example("chain-of-responsibility", "Chain of Responsibility", BEHAVIORAL,
                    ChainOfResponsibilityDemo::run),
            example("command", "Command", BEHAVIORAL, CommandPatternDemo::run),
            example("iterator", "Iterator", BEHAVIORAL, IteratorPatternDemo::run),
            example("mediator", "Mediator", BEHAVIORAL, MediatorPatternDemo::run),
            example("memento", "Memento", BEHAVIORAL, MementoPatternDemo::run),
            example("observer", "Observer", BEHAVIORAL, ObserverPatternDemo::run),
            example("state", "State", BEHAVIORAL, StatePatternDemo::run),
            example("strategy", "Strategy", BEHAVIORAL, StrategyPatternDemo::run),
            example("template-method", "Template Method", BEHAVIORAL, TemplateMethodPatternDemo::run),
            example("visitor", "Visitor", BEHAVIORAL, VisitorPatternDemo::run)
    );

    private static final Map<String, PatternExample> BY_SLUG = indexBySlug(EXAMPLES);
    private static final Map<PatternFamily, List<PatternExample>> BY_FAMILY = indexByFamily(EXAMPLES);

    private PatternCatalog() {
    }

    public static List<PatternExample> all() {
        return EXAMPLES;
    }

    public static List<PatternExample> byFamily(PatternFamily family) {
        Objects.requireNonNull(family, "family boş olamaz");
        return BY_FAMILY.get(family);
    }

    public static Optional<PatternExample> find(String slug) {
        if (slug == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_SLUG.get(normalize(slug)));
    }

    /**
     * {@code all}, aile slug'ı veya desen slug'ı için sıralı ve değiştirilemez seçim döndürür.
     */
    public static List<PatternExample> select(String selector) {
        String normalized = normalize(selector);
        if (normalized.isEmpty() || normalized.equals("all")) {
            return all();
        }

        Optional<PatternFamily> family = PatternFamily.from(selector);
        if (family.isPresent()) {
            return byFamily(family.orElseThrow());
        }

        return find(normalized)
                .map(List::of)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bilinmeyen seçim: '%s'. Aile veya desen adlarını görmek için --list kullan."
                                .formatted(selector)
                ));
    }

    public static int run(String selector, PrintStream output) {
        return run(select(selector), output);
    }

    /**
     * Önceden çözülmüş seçimi verilen sırayla çalıştırır.
     *
     * <p>Seçim ile execution'ın ayrılması, CLI katmanının yalnız selector hatalarını
     * kullanıcı girdisi hatası olarak ele almasını sağlar. Liste çağrı başında
     * kopyalanır; null kayıtlar erken reddedilir ve çalışırken dışarıdan
     * değiştirilemez.</p>
     */
    public static int run(List<PatternExample> selection, PrintStream output) {
        Objects.requireNonNull(selection, "selection boş olamaz");
        Objects.requireNonNull(output, "output boş olamaz");
        List<PatternExample> selected = List.copyOf(selection);
        PatternFamily currentFamily = null;

        for (PatternExample example : selected) {
            if (currentFamily != example.family()) {
                currentFamily = example.family();
                printFamilyHeader(currentFamily, output);
            }
            output.printf("--- %s [%s] ---%n", example.displayName(), example.slug());
            example.execute();
            output.println();
        }

        return selected.size();
    }

    public static void printCatalog(PrintStream output) {
        Objects.requireNonNull(output, "output boş olamaz");
        output.println("Kullanım: all | <aile> | <pattern-slug> | --list | --help");
        for (PatternFamily family : PatternFamily.values()) {
            output.printf("%n%s (%s) — %s%n",
                    family.turkishName(), family.slug(), family.purpose());
            byFamily(family).forEach(example ->
                    output.printf("  %-28s %s%n", example.slug(), example.displayName())
            );
        }
    }

    private static PatternExample example(
            String slug,
            String displayName,
            PatternFamily family,
            Runnable demo
    ) {
        return new PatternExample(slug, displayName, family, demo);
    }

    private static Map<String, PatternExample> indexBySlug(List<PatternExample> examples) {
        Map<String, PatternExample> index = new LinkedHashMap<>();
        for (PatternExample example : examples) {
            PatternExample previous = index.put(example.slug(), example);
            if (previous != null) {
                throw new IllegalStateException("Tekrarlanan pattern slug'ı: " + example.slug());
            }
        }
        return Collections.unmodifiableMap(index);
    }

    private static Map<PatternFamily, List<PatternExample>> indexByFamily(
            List<PatternExample> examples
    ) {
        Map<PatternFamily, List<PatternExample>> grouped = examples.stream()
                .collect(Collectors.groupingBy(
                        PatternExample::family,
                        () -> new EnumMap<>(PatternFamily.class),
                        Collectors.toUnmodifiableList()
                ));

        for (PatternFamily family : PatternFamily.values()) {
            grouped.putIfAbsent(family, List.of());
        }
        return Collections.unmodifiableMap(grouped);
    }

    private static void printFamilyHeader(PatternFamily family, PrintStream output) {
        output.printf("=== %s / %s ===%n", family.turkishName().toUpperCase(), family.slug().toUpperCase());
        output.printf("%s%n%n", family.purpose());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
    }
}
