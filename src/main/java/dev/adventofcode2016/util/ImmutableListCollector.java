package dev.adventofcode2016.util;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector that builds a Guava ImmutableList from the input.  Guava 21.0 contains built-in
 * support for Java 8, but it isn't out yet.
 *
 * @param <E> element type
 */
public class ImmutableListCollector<E> implements Collector<E, ImmutableList.Builder<E>, ImmutableList<E>> {
  @Override
  public Supplier<ImmutableList.Builder<E>> supplier() {
    return ImmutableList::builder;
  }

  @Override
  public BiConsumer<ImmutableList.Builder<E>, E> accumulator() {
    return ImmutableList.Builder::add;
  }

  @Override
  public BinaryOperator<ImmutableList.Builder<E>> combiner() {
    return (builder1, builder2) -> builder1.addAll(builder2.build());
  }

  @Override
  public Function<ImmutableList.Builder<E>, ImmutableList<E>> finisher() {
    return ImmutableList.Builder::build;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }
}
