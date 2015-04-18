/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.util.stream.builder;

import com.speedment.util.stream.builder.pipeline.BasePipeline;
import com.speedment.util.stream.builder.streamterminator.StreamTerminator;
import com.speedment.util.stream.builder.action.reference.DistinctAction;
import com.speedment.util.stream.builder.action.reference.FilterAction;
import com.speedment.util.stream.builder.action.reference.FlatMapAction;
import com.speedment.util.stream.builder.action.reference.FlatMapToDoubleAction;
import com.speedment.util.stream.builder.action.reference.FlatMapToIntAction;
import com.speedment.util.stream.builder.action.reference.FlatMapToLongAction;
import com.speedment.util.stream.builder.action.reference.LimitAction;
import com.speedment.util.stream.builder.action.reference.MapAction;
import com.speedment.util.stream.builder.action.reference.MapToDoubleAction;
import com.speedment.util.stream.builder.action.reference.MapToIntAction;
import com.speedment.util.stream.builder.action.reference.MapToLongAction;
import com.speedment.util.stream.builder.action.reference.PeekAction;
import com.speedment.util.stream.builder.action.reference.SkipAction;
import com.speedment.util.stream.builder.action.reference.SortedAction;
import com.speedment.util.stream.builder.pipeline.ReferencePipeline;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *
 * @author pemi
 */
public class ReferenceStreamBuilder<T> extends BaseStreamBuilder<ReferenceStreamBuilder<T>> implements Stream<T> {

    public ReferenceStreamBuilder(BasePipeline<?> pipeline, final StreamTerminator streamTerminator) {
        super(pipeline, streamTerminator);
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        return append(new FilterAction(predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return new ReferenceStreamBuilder<R>(pipeline, streamTerminator).append(new MapAction<>(mapper));
        //return new ReferenceStreamBuilder<R>(optimizer, actions).append(new Action<>((Stream<T> t) -> t.map(mapper), Stream.class));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return new IntStreamBuilder(pipeline, streamTerminator).append(new MapToIntAction<>(mapper));
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return new LongStreamBuilder(pipeline, streamTerminator).append(new MapToLongAction<>(mapper));
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return new DoubleStreamBuilder(pipeline, streamTerminator).append(new MapToDoubleAction<>(mapper));
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new ReferenceStreamBuilder<R>(pipeline, streamTerminator).append(new FlatMapAction<>(mapper));
    }

    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return new IntStreamBuilder(pipeline, streamTerminator).append(new FlatMapToIntAction<>(mapper));
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return new LongStreamBuilder(pipeline, streamTerminator).append(new FlatMapToLongAction<>(mapper));
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return new DoubleStreamBuilder(pipeline, streamTerminator).append(new FlatMapToDoubleAction<>(mapper));
    }

    @Override
    public Stream<T> distinct() {
        return append(new DistinctAction());
    }

    @Override
    public Stream<T> sorted() {
        return append(new SortedAction());
    }

    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
        return append(new SortedAction(comparator));
    }

    @Override
    public Stream<T> peek(Consumer<? super T> action) {
        return append(new PeekAction(action));
    }

    @Override
    public Stream<T> limit(long maxSize) {
        return append(new LimitAction(maxSize));
    }

    @Override
    public Stream<T> skip(long n) {
        return append(new SkipAction(n));
    }

    // Terminal operations
    @Override
    public void forEach(Consumer<? super T> action) {
        streamTerminator.forEach((ReferencePipeline<T>) pipeline, action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        streamTerminator.forEachOrdered((ReferencePipeline<T>) pipeline, action);
    }

    @Override
    public Object[] toArray() {
        return streamTerminator.toArray((ReferencePipeline<T>) pipeline);
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return streamTerminator.toArray((ReferencePipeline<T>) pipeline, generator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return streamTerminator.reduce((ReferencePipeline<T>) pipeline, identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return streamTerminator.reduce((ReferencePipeline<T>) pipeline, accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return streamTerminator.reduce((ReferencePipeline<T>) pipeline, identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return streamTerminator.collect((ReferencePipeline<T>) pipeline, supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return streamTerminator.collect((ReferencePipeline<T>) pipeline, collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return streamTerminator.min((ReferencePipeline<T>) pipeline, comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return streamTerminator.max((ReferencePipeline<T>) pipeline, comparator);
    }

    @Override
    public long count() {
        return streamTerminator.count((ReferencePipeline<T>) pipeline);
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return streamTerminator.anyMatch((ReferencePipeline<T>) pipeline, predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return streamTerminator.allMatch((ReferencePipeline<T>) pipeline, predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return streamTerminator.noneMatch((ReferencePipeline<T>) pipeline, predicate);
    }

    @Override
    public Optional<T> findFirst() {
        return streamTerminator.findFirst((ReferencePipeline<T>) pipeline);
    }

    @Override
    public Optional<T> findAny() {
        return streamTerminator.findAny((ReferencePipeline<T>) pipeline);
    }

    @Override
    public Iterator<T> iterator() {
        return streamTerminator.iterator((ReferencePipeline<T>) pipeline);
    }

    @Override
    public Spliterator<T> spliterator() {
        return streamTerminator.spliterator((ReferencePipeline<T>) pipeline);
    }

}
