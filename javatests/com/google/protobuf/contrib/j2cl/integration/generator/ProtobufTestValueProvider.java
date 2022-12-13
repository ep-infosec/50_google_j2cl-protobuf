/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.protobuf.contrib.j2cl.integration.generator;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import java.util.function.Function;

/** Wraps a {@link Type} with a value provider/renderer for both Mutable JSPB and Immutable JSPB. */
public final class ProtobufTestValueProvider<T> {
  private static final String[] testStrings = {"alpha", "bravo", "charlie"};

  private static final ProtobufTestValueProvider<String> STRING_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.STRING,
          /* isNullable= */ true,
          testStrings,
          (value) -> String.format("\"%s\"", value));

  private static final ProtobufTestValueProvider<Double> DOUBLE_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.DOUBLE,
          /* isNullable= */ false,
          new Double[] {1.333333, 2.66666, 3.99999},
          (value) -> String.format("%fd", value));

  private static final ProtobufTestValueProvider<Float> FLOAT_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.FLOAT,
          /* isNullable= */ false,
          new Float[] {1.333333f, 2.66666f, 3.99999f},
          (value) -> String.format("%ff", value));

  private static final ProtobufTestValueProvider<Integer> INT32_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.INT32,
          /* isNullable= */ false,
          new Integer[] {Integer.MIN_VALUE, 2, Integer.MAX_VALUE},
          String::valueOf);

  private static final ProtobufTestValueProvider<Integer> UINT32_PROVIDER =
      // Use -1 to force the sign bit.
      new ProtobufTestValueProvider<>(
          Type.UINT32, /* isNullable= */ false, new Integer[] {0, 1, -1}, String::valueOf);

  private static final ProtobufTestValueProvider<Long> INT64_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.INT64,
          /* isNullable= */ false,
          // Shift sufficiently to be out of int32 range.
          new Long[] {(1L << 50) + 1, -(1L << 50) - 2, (1L << 50) + 3},
          (value) -> String.format("%dL", value));

  private static final ProtobufTestValueProvider<Long> UINT64_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.UINT64,
          /* isNullable= */ false,
          // Shift sufficiently to be out of int32 range.
          // Use -1 to force the sign bit.
          new Long[] {(1L << 33) + 1, (1L << 33) + 2, -1L},
          (value) -> String.format("%dL", value));

  private static final ProtobufTestValueProvider<Boolean> BOOLEAN_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.BOOL, /* isNullable= */ false, new Boolean[] {true, false, false}, String::valueOf);

  private static final ProtobufTestValueProvider<String> MESSAGE_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.MESSAGE,
          /* isNullable= */ true,
          testStrings,
          (value) ->
              String.format(
                  "MapTestProto.NestedMessage.newBuilder().setFoo(\"%s\").build()", value));

  private static final ProtobufTestValueProvider<String> BYTES_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.BYTES,
          /* isNullable= */ true,
          testStrings,
          (value) ->
              String.format(
                  "ByteString.copyFromUtf8(\"%s\")",
                  BaseEncoding.base64().encode(value.getBytes(UTF_8))));

  private static final ProtobufTestValueProvider<Integer> ENUM_PROVIDER =
      new ProtobufTestValueProvider<>(
          Type.ENUM,
          /* isNullable= */ true,
          new Integer[] {1, 2, 3},
          (value) -> String.format("MapTestProto.TestEnum.forNumber(%d)", value));

  public static ProtobufTestValueProvider<?> forType(Type type) {
    switch (type) {
      case DOUBLE:
        return DOUBLE_PROVIDER;
      case FLOAT:
        return FLOAT_PROVIDER;
      case UINT32:
        return UINT32_PROVIDER;
      case INT32:
        return INT32_PROVIDER;
      case UINT64:
        return UINT64_PROVIDER;
      case INT64:
        return INT64_PROVIDER;
      case BOOL:
        return BOOLEAN_PROVIDER;
      case STRING:
        return STRING_PROVIDER;
      case MESSAGE:
        return MESSAGE_PROVIDER;
      case BYTES:
        return BYTES_PROVIDER;
      case ENUM:
        return ENUM_PROVIDER;
      case SINT32:
      case FIXED32:
      case SFIXED32:
      case SFIXED64:
      case FIXED64:
      case SINT64:
        throw new IllegalArgumentException("Type is covered by by other tests cases");
      case GROUP:
        throw new IllegalArgumentException("Unsupported type");
    }
    throw new AssertionError("Switch was exhaustive.");
  }

  private final Type type;
  private final boolean isNullable;
  private final T[] testValues;
  private final Function<T, String> valueRenderer;

  private ProtobufTestValueProvider(
      Type type, boolean isNullable, T[] testValues, Function<T, String> valueRenderer) {
    this.type = type;
    this.isNullable = isNullable;
    this.testValues = testValues;
    this.valueRenderer = valueRenderer;
  }

  public String getStem() {
    return UPPER_UNDERSCORE.to(UPPER_CAMEL, type.name());
  }

  public String renderValue(int index) {
    return valueRenderer.apply(testValues[index]);
  }

  public boolean isNullable() {
    return isNullable;
  }
}
